package me.kcybulski.nexum.eventstore.aggregates

import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfiguration
import me.kcybulski.nexum.eventstore.publishing.PublishEventConfiguration.Companion.publishConfiguration
import me.kcybulski.nexum.eventstore.publishing.PublishingFacade
import me.kcybulski.nexum.eventstore.reader.EventsQuery.Companion.query
import kotlin.reflect.KClass
import kotlin.streams.toList
import java.util.stream.Stream as JavaStream

class AggregatesFacade(
    private val factoriesRegistry: FactoriesRegistry,
    private val eventsFacade: EventsFacade,
    private val publishingFacade: PublishingFacade
) {

    fun <T : AggregateRoot<T>> store(aggregate: T, streamId: StreamId): T = aggregate.unpublishedEvents
        .onEach { publishingFacade.publish(it, streamId, publishConfiguration()) }
        .apply { clear() }
        .let { aggregate }

    fun <T : AggregateRoot<T>, E : Any> register(eventType: KClass<E>, factory: AggregateFactory<T, E>) =
        factoriesRegistry
            .save(eventType, factory)

    fun <T : AggregateRoot<T>, E : Any> new(eventType: KClass<E>, event: E): T? = factoriesRegistry.get<T, E>(eventType)
        ?.onCreate(event)
        ?.event(event)

    fun <T : AggregateRoot<T>, E : Any> load(streamId: StreamId): T? =
        query { stream(streamId) }
            .let(eventsFacade::read)
            .applyEvents<T, E> { event: E -> new(event::class as KClass<E>, event) }

    fun <T : AggregateRoot<T>, E : Any> with(streamId: StreamId, modifier: (T) -> Unit): T? =
        load<T, E>(streamId)?.apply(modifier)?.let { store(it, streamId) }

    fun <T : AggregateRoot<T>, E : Any> with(streamId: StreamId, creator: E, modifier: (T) -> Unit): T? =
        new<T, E>(creator::class as KClass<E>, creator)?.apply(modifier)?.let { store(it, streamId) }
}

private fun <T : AggregateRoot<T>, E : Any> JavaStream<DomainEvent<*>>.applyEvents(factory: (E) -> T?): T? =
    map { it.payload }
        .toList()
        .takeIf { it.isNotEmpty() }
        ?.run { fold(factory(first() as E)) { agg, event -> agg?.applyEvent(event) } }
