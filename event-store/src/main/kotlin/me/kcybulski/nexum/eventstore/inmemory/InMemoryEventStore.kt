package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.EventStore

object InMemoryEventStore {

    fun create(): EventStore = EventStore(
        InMemoryHandlersRepository(),
        InMemoryEventsRepository()
    )

}
