package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.DomainEvent
import me.kcybulski.nexum.eventstore.EventsRepository

internal class InMemoryEventsRepository : EventsRepository {

    private val streams: MutableMap<String, List<DomainEvent<*>>> = mutableMapOf()

    override fun <T> save(event: DomainEvent<T>) {
        streams.merge(event.stream, mutableListOf(event)) { a, b -> a + b}
    }

    override fun loadStream(stream: String): List<DomainEvent<*>> {
        return streams[stream] ?: emptyList()
    }
}
