package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.EventsFactory

object InMemoryEventStore {

    fun create(): EventStore {
        val eventsManager = EventsFacade(InMemoryEventsRepository(), EventsFactory())
        return EventStore(
            InMemoryHandlersRepository(),
            eventsManager
        )
    }
}
