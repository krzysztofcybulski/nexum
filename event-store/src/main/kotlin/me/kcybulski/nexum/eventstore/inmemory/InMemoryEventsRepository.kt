package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.events.EventsRepository
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId

internal class InMemoryEventsRepository : EventsRepository {

    private val streams: MutableMap<Stream, List<DomainEvent<*>>> = mutableMapOf()

    override fun <T> save(event: DomainEvent<T>) {
        streams.merge(event.stream, mutableListOf(event)) { a, b -> a + b }
    }

    override fun loadStream(stream: StreamId): List<DomainEvent<*>> {
        return streams[stream] ?: emptyList()
    }
}
