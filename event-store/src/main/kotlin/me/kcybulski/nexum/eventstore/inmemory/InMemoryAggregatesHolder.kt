package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.AggregateRoot
import me.kcybulski.nexum.eventstore.AggregatesHolder
import me.kcybulski.nexum.eventstore.DomainEvent
import me.kcybulski.nexum.eventstore.EventToPersist
import me.kcybulski.nexum.eventstore.EventsRepository
import java.time.Clock

class InMemoryAggregatesHolder(
    private val eventsRepository: EventsRepository,
    private val clock: Clock = Clock.systemUTC()
) : AggregatesHolder {

    private val events: MutableMap<AggregateRoot<*>, List<EventToPersist<*>>> = mutableMapOf()

    override fun <T : AggregateRoot<*>, E> addEvent(aggregate: T, event: E) {
        events.merge(aggregate, listOf(EventToPersist(event))) { a, b -> a + b }
    }

    override fun <T : AggregateRoot<*>> store(aggregate: T, stream: String): T {
        (events.remove(aggregate) ?: emptyList())
            .map { domainEvent(it, stream) }
            .forEach { eventsRepository.save(it) }
        return aggregate
    }

    private fun <T> domainEvent(eventToPersist: EventToPersist<T>, stream: String): DomainEvent<T> =
        DomainEvent(
            payload = eventToPersist.payload,
            stream = stream,
            timestamp = clock.instant()
        )
}
