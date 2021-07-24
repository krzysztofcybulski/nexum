package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot
import me.kcybulski.nexum.eventstore.aggregates.AggregatesHolder
import me.kcybulski.nexum.eventstore.aggregates.EventToPersist
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.Stream

class InMemoryAggregatesHolder(
    private val eventsManager: EventsFacade
) : AggregatesHolder {

    private val events: MutableMap<AggregateRoot<*>, List<EventToPersist<*>>> = mutableMapOf()

    override fun <T : AggregateRoot<*>, E> addEvent(aggregate: T, event: E) {
        events.merge(aggregate, listOf(EventToPersist(event))) { a, b -> a + b }
    }

    override fun <T : AggregateRoot<*>> store(aggregate: T, stream: Stream): T {
        (events.remove(aggregate) ?: emptyList())
            .forEach { eventsManager.save(it.payload, stream) }
        return aggregate
    }
}
