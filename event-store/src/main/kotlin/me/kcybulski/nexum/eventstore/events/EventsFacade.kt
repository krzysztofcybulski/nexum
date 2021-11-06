package me.kcybulski.nexum.eventstore.events

import kotlin.streams.toList
import java.util.stream.Stream as JavaStream

class EventsFacade(
    private val eventsRepository: EventsRepository,
    private val eventsFactory: EventsFactory
) {

    fun <T : Any> save(payload: T, stream: Stream) = eventsFactory
        .create(payload, stream)
        .let(eventsRepository::save)

    fun read(query: EventsQuery): JavaStream<DomainEvent<*>> = eventsRepository
        .query(query)

    fun <T> project(init: T, query: EventsQuery, reduce: (T, Any) -> T): T =
        read(query).toList().mapNotNull(DomainEvent<*>::payload).fold(init, reduce)

}
