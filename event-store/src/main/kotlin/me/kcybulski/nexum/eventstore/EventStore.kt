package me.kcybulski.nexum.eventstore

import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot
import me.kcybulski.nexum.eventstore.aggregates.AggregatesHolder
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.NoStream
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.handlers.HandlersRepository
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfiguration
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfiguration.Companion.configuration
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfigurationBuilder
import me.kcybulski.nexum.eventstore.publishing.PublishingError
import me.kcybulski.nexum.eventstore.publishing.PublishingUncheckedException
import me.kcybulski.nexum.eventstore.subscribing.AllTypesHandler
import me.kcybulski.nexum.eventstore.subscribing.BasicSubscription
import me.kcybulski.nexum.eventstore.subscribing.EventHandler
import me.kcybulski.nexum.eventstore.subscribing.EventTypeHandler
import me.kcybulski.nexum.eventstore.subscribing.Subscription
import kotlin.reflect.KClass

class EventStore(
    private val handlersRepository: HandlersRepository,
    private val eventsManager: EventsFacade,
    private val aggregatesHolder: AggregatesHolder,
) {
    fun <T : Any> subscribe(event: KClass<T>, handler: (T) -> Unit): Subscription<T> = EventTypeHandler(event, handler)
        .let(handlersRepository::register)
        .let { BasicSubscription(it, this) }

    fun subscribeAll(handler: (Any) -> Unit): Subscription<Any> = AllTypesHandler(handler)
        .let(handlersRepository::register)
        .let { BasicSubscription(it, this) }

    fun <T : Any> publish(
        event: T,
        stream: Stream = NoStream,
        configurationBuilder: PublishEventConfigurationBuilder.() -> Unit = {}
    ) {
        append(event, stream)
        fireEventHandlers(event, configuration(configurationBuilder))
    }

    fun <T> append(event: T, stream: Stream = NoStream) {
        eventsManager.save(event, stream)
    }

    fun <T> unsubscribe(handler: EventHandler<T>) {
        handlersRepository.unregister(handler)
    }

    fun <T : AggregateRoot<T>> load(stream: StreamId, factory: (AggregatesHolder) -> T): T =
        factory(aggregatesHolder).applyAllEvents(eventsManager.loadStream(stream))

    fun <T : AggregateRoot<T>> new(factory: (AggregatesHolder) -> T): T = factory(aggregatesHolder)

    private fun <T : Any> fireEventHandlers(event: T, configuration: PublishEventConfiguration) = handlersRepository
        .findHandlers(event::class)
        .forEach { handler -> event.tryOrElse(handler) { configuration.errorHandler(it) } }

    internal fun unsubscribeAll() {
        handlersRepository.unregisterAll()
    }

}

private fun <T : Any> T.tryOrElse(func: (T) -> Unit, errorHandler: (PublishingError) -> Unit) = try {
    func(this)
} catch (e: RuntimeException) {
    errorHandler(PublishingUncheckedException(e))
}

private fun <T : AggregateRoot<T>> T.applyAllEvents(events: List<DomainEvent<*>>): T =
    events.fold(this) { agg, event -> agg.apply(event.payload) }
