package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventsRepository
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.reader.EventsQuery
import java.util.stream.Stream as JavaStream

internal class InMemoryEventsRepository : EventsRepository {

    private val streams: MutableMap<Stream, List<DomainEvent<*>>> = mutableMapOf()

    override fun <T> save(event: DomainEvent<T>) {
        streams.merge(event.stream, mutableListOf(event)) { a, b -> a + b }
    }

    override fun query(query: EventsQuery): JavaStream<DomainEvent<*>> = query
        .streams
        .flatMap { streams[it] ?: emptyList() }
        .sortedBy { it.timestamp }
        .stream()
}
