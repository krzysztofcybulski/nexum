package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.events.EventsFactory
import me.kcybulski.nexum.eventstore.events.EventsFacade

object InMemoryEventStore {

    fun create(): EventStore {
        val eventsManager = EventsFacade(InMemoryEventsRepository(), EventsFactory())
        return EventStore(
            InMemoryHandlersRepository(),
            eventsManager,
            InMemoryAggregatesHolder(eventsManager)
        )
    }
}
