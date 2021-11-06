package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventStreamDirection.BACKWARD
import me.kcybulski.nexum.eventstore.events.EventsQuery
import me.kcybulski.nexum.eventstore.events.EventsRepository
import me.kcybulski.nexum.eventstore.events.Stream
import java.util.stream.Stream as JavaStream

internal class InMemoryEventsRepository : EventsRepository {

    private val streams: MutableMap<Stream, List<DomainEvent<*>>> = mutableMapOf()

    override fun <T : Any> save(event: DomainEvent<T>) {
        streams.merge(event.stream, mutableListOf(event)) { a, b -> a + b }
    }

    override fun query(query: EventsQuery): JavaStream<DomainEvent<*>> = query
        .streams
        .flatMap { streams[it] ?: emptyList() }
        .sortedBy { it.timestamp }
        .ifQuery(query.direction == BACKWARD) { it.reversed() }
        .ifQuery(query.limit != null) { it.take(query.limit!!) }
        .stream()
}

private fun <E : DomainEvent<*>> List<E>.ifQuery(condition: Boolean, handler: (List<E>) -> List<E>) =
    if (condition) handler(this) else this
