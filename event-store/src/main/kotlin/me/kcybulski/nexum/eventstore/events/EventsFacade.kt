package me.kcybulski.nexum.eventstore.events

import me.kcybulski.nexum.eventstore.reader.EventsQuery
import java.util.stream.Stream as JavaStream

class EventsFacade(
    private val eventsRepository: EventsRepository,
    private val eventsFactory: EventsFactory
) {

    fun <T> save(payload: T, stream: Stream) = eventsFactory
        .create(payload, stream)
        .let(eventsRepository::save)

    fun read(query: EventsQuery): JavaStream<DomainEvent<*>> = eventsRepository
        .query(query)

}
