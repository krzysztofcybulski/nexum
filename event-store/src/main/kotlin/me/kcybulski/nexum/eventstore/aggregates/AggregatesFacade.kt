package me.kcybulski.nexum.eventstore.aggregates

import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.reader.EventsQuery.Companion.query
import java.util.stream.Collectors
import kotlin.reflect.KClass
import kotlin.streams.toList
import java.util.stream.Stream as JavaStream

class AggregatesFacade(
    private val factoriesRegistry: FactoriesRegistry,
    private val eventsFacade: EventsFacade
) {

    fun <T : AggregateRoot<T>> store(aggregate: T, streamId: StreamId) = aggregate.unpublishedEvents
        .onEach { eventsFacade.save(it, streamId) }
        .apply { clear() }

    fun <T : AggregateRoot<T>, E : Any> register(eventType: KClass<E>, factory: AggregateFactory<T, E>) =
        factoriesRegistry
            .save(eventType, factory)

    fun <T : AggregateRoot<T>, E : Any> new(eventType: KClass<E>, event: E): T? = factoriesRegistry.get<T, E>(eventType)
        ?.onCreate(event)
        ?.event(event)

    fun <T : AggregateRoot<T>, E : Any> load(streamId: StreamId): T? =
        query { stream(streamId) }
            .let(eventsFacade::read)
            .toList()
            .takeIf { it.isNotEmpty() }
            ?.let { list ->
                val first: E = list.first().payload as E
                return new<T, E>(first::class as KClass<E>, first)
                    ?.let { list.fold(it) { agg, event -> agg.apply(event) } }
            }

    @Deprecated("Use methods with aggregate factories")
    fun <T : AggregateRoot<T>> load(streamId: StreamId, factory: () -> T): T =
        query { stream(streamId) }
            .let(eventsFacade::read)
            .let(factory()::applyAllEvents)

    @Deprecated("Use methods with aggregate factories")
    fun <T : AggregateRoot<T>> with(streamId: StreamId, factory: () -> T, action: T.() -> Unit) =
        load(streamId, factory)
            .also { action(it) }
            .also { store(it, streamId) }

}

private fun <T : AggregateRoot<T>> T.applyAllEvents(events: JavaStream<DomainEvent<*>>): T =
    events.collect(Collectors.toList()).fold(this) { agg, event -> agg.apply(event.payload) }
