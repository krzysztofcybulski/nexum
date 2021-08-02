package me.kcybulski.nexum.eventstore

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot
import me.kcybulski.nexum.eventstore.aggregates.AggregatesHolder
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.NoStream
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.handlers.HandlersRepository
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfiguration
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfiguration.Companion.publishConfiguration
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfigurationBuilder
import me.kcybulski.nexum.eventstore.publishing.PublishingError
import me.kcybulski.nexum.eventstore.publishing.PublishingUncheckedException
import me.kcybulski.nexum.eventstore.reader.EventsQuery.Companion.query
import me.kcybulski.nexum.eventstore.reader.EventsQueryBuilder
import me.kcybulski.nexum.eventstore.subscribing.AllTypesHandler
import me.kcybulski.nexum.eventstore.subscribing.BasicSubscription
import me.kcybulski.nexum.eventstore.subscribing.EventHandler
import me.kcybulski.nexum.eventstore.subscribing.EventTypeHandler
import me.kcybulski.nexum.eventstore.subscribing.Subscription
import java.util.stream.Collectors.toList
import kotlin.reflect.KClass
import kotlin.streams.toList
import java.util.stream.Stream as JavaStream

class EventStore(
    private val handlersRepository: HandlersRepository,
    private val eventsManager: EventsFacade,
    private val aggregatesHolder: AggregatesHolder,
) {
    fun <T : Any> subscribe(event: KClass<T>, handler: suspend (T) -> Unit): Subscription<T> =
        EventTypeHandler(event, handler)
            .let(handlersRepository::register)
            .let { BasicSubscription(it, this) }

    fun subscribeAll(handler: suspend (Any) -> Unit): Subscription<Any> = AllTypesHandler(handler)
        .let(handlersRepository::register)
        .let { BasicSubscription(it, this) }

    suspend fun <T : Any> publishAsync(
        event: T,
        stream: Stream = NoStream,
        configurationBuilder: PublishEventConfigurationBuilder.() -> Unit = {}
    ) {
        append(event, stream)
        fireEventHandlers(event, publishConfiguration(configurationBuilder))
    }

    fun <T : Any> publish(
        event: T,
        stream: Stream = NoStream,
        configurationBuilder: PublishEventConfigurationBuilder.() -> Unit = {}
    ) = runBlocking { publishAsync(event, stream, configurationBuilder) }

    fun <T> append(event: T, stream: Stream = NoStream) {
        eventsManager.save(event, stream)
    }

    fun <T> unsubscribe(handler: EventHandler<T>) {
        handlersRepository.unregister(handler)
    }

    fun <T : AggregateRoot<T>> load(streamId: StreamId, factory: (AggregatesHolder) -> T): T =
        query { stream(streamId) }
            .let(eventsManager::read)
            .let(factory(aggregatesHolder)::applyAllEvents)

    fun <T : AggregateRoot<T>> new(factory: (AggregatesHolder) -> T): T = factory(aggregatesHolder)

    fun read(queryBuilder: EventsQueryBuilder.() -> Unit): JavaStream<DomainEvent<*>> =
        eventsManager.read(query(queryBuilder))

    fun <T> project(init: T, queryBuilder: EventsQueryBuilder.() -> Unit, reduce: (T, Any) -> T): T =
        read(queryBuilder).toList().mapNotNull(DomainEvent<*>::payload).fold(init, reduce)

    private suspend fun <T : Any> fireEventHandlers(event: T, configuration: PublishEventConfiguration) =
        coroutineScope {
            handlersRepository
                .findHandlers(event::class)
                .forEach { handler -> launch { event.tryOrElse(handler) { configuration.errorHandler(it) } } }
        }

    internal fun unsubscribeAll() {
        handlersRepository.unregisterAll()
    }
}

private suspend fun <T : Any> T.tryOrElse(func: suspend (T) -> Unit, errorHandler: (PublishingError) -> Unit) = try {
    func(this)
} catch (e: RuntimeException) {
    errorHandler(PublishingUncheckedException(e))
}

private fun <T : AggregateRoot<T>> T.applyAllEvents(events: JavaStream<DomainEvent<*>>): T =
    events.collect(toList()).fold(this) { agg, event -> agg.apply(event.payload) }
