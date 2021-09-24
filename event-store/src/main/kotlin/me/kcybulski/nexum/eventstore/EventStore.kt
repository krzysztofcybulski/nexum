package me.kcybulski.nexum.eventstore

import me.kcybulski.nexum.eventstore.aggregates.AggregateFactory
import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot
import me.kcybulski.nexum.eventstore.aggregates.AggregatesFacade
import me.kcybulski.nexum.eventstore.aggregates.FactoriesRegistry
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.EventsFactory
import me.kcybulski.nexum.eventstore.events.EventsRepository
import me.kcybulski.nexum.eventstore.events.NoStream
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.handlers.HandlersRegistry
import me.kcybulski.nexum.eventstore.inmemory.InMemoryFactoriesRegistry
import me.kcybulski.nexum.eventstore.inmemory.InMemoryHandlersRegistry
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfiguration.Companion.publishConfiguration
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfigurationBuilder
import me.kcybulski.nexum.eventstore.publishing.PublishingFacade
import me.kcybulski.nexum.eventstore.reader.EventsQuery.Companion.query
import me.kcybulski.nexum.eventstore.reader.EventsQueryBuilder
import me.kcybulski.nexum.eventstore.subscribing.EventHandler
import me.kcybulski.nexum.eventstore.subscribing.Subscription
import me.kcybulski.nexum.eventstore.subscribing.SubscriptionFacade
import kotlin.reflect.KClass
import java.util.stream.Stream as JavaStream

class EventStore(
    private val subscriptions: SubscriptionFacade,
    private val publishing: PublishingFacade,
    @PublishedApi internal val aggregates: AggregatesFacade,
    private val eventsFacade: EventsFacade
) {
    fun <T : Any> subscribe(event: KClass<T>, handler: suspend (T) -> Unit): Subscription<T> = subscriptions
        .subscribe(event, handler)

    fun subscribeAll(handler: suspend (Any) -> Unit): Subscription<Any> = subscriptions
        .subscribeAll(handler)

    suspend fun <T : Any> publishAsync(
        event: T,
        stream: Stream = NoStream,
        configurationBuilder: PublishEventConfigurationBuilder.() -> Unit = {}
    ) = publishing.publishAsync(event, stream, publishConfiguration(configurationBuilder))

    fun <T : Any> publish(
        event: T,
        stream: Stream = NoStream,
        configurationBuilder: PublishEventConfigurationBuilder.() -> Unit = {}
    ) = publishing.publish(event, stream, publishConfiguration(configurationBuilder))

    fun <T : Any> append(event: T, stream: Stream = NoStream) = publishing.append(event, stream)

    fun <T> unsubscribe(handler: EventHandler<T>) = subscriptions.unsubscribe(handler)

    inline fun <T : AggregateRoot<T>, reified E : Any> register(factory: AggregateFactory<T, E>) = aggregates
        .register(E::class, factory)

    inline fun <T : AggregateRoot<T>, reified E : Any> new(event: E): T? = aggregates.new<T, E>(E::class, event)

    fun <T : AggregateRoot<T>> store(aggregate: T, streamId: StreamId) = aggregates.store(aggregate, streamId)

    fun <T : AggregateRoot<T>, E : Any> load(streamId: StreamId): T? = aggregates.load<T, E>(streamId)

    fun <T : AggregateRoot<T>> load(streamId: StreamId, factory: () -> T): T = aggregates.load(streamId, factory)

    fun <T : AggregateRoot<T>> with(streamId: StreamId, factory: () -> T, action: T.() -> Unit) =
        aggregates.with(streamId, factory, action)

    fun read(queryBuilder: EventsQueryBuilder.() -> Unit): JavaStream<DomainEvent<*>> =
        eventsFacade.read(query(queryBuilder))

    fun <T> project(init: T, queryBuilder: EventsQueryBuilder.() -> Unit, reduce: (T, Any) -> T): T =
        eventsFacade.project(init, query(queryBuilder), reduce)

    companion object {
        fun create(
            eventsRepository: EventsRepository,
            handlersRegistry: HandlersRegistry = InMemoryHandlersRegistry(),
            factoriesRegistry: FactoriesRegistry = InMemoryFactoriesRegistry()
        ): EventStore {
            val eventsFacade = EventsFacade(eventsRepository, EventsFactory())
            return EventStore(
                subscriptions = SubscriptionFacade(handlersRegistry),
                publishing = PublishingFacade(handlersRegistry, eventsFacade),
                aggregates = AggregatesFacade(factoriesRegistry, eventsFacade),
                eventsFacade = eventsFacade
            )
        }
    }
}
