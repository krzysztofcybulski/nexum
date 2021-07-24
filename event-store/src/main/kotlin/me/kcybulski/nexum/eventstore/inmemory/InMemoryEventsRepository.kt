package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.DomainEvent
import me.kcybulski.nexum.eventstore.EventsRepository
import me.kcybulski.nexum.eventstore.Stream

internal class InMemoryEventsRepository : EventsRepository {

    private val streams: MutableMap<Stream, List<DomainEvent<*>>> = mutableMapOf()

    override fun <T> save(event: DomainEvent<T>) {
        streams.merge(event.stream, mutableListOf(event)) { a, b -> a + b }
    }

    override fun loadStream(stream: Stream): List<DomainEvent<*>> {
        return streams[stream] ?: emptyList()
    }
}
