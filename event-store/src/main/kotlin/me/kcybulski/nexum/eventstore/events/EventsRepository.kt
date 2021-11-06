package me.kcybulski.nexum.eventstore.events

import java.util.stream.Stream as JavaStream

interface EventsRepository {

    fun <T : Any> save(event: DomainEvent<T>)
    fun query(query: EventsQuery): JavaStream<DomainEvent<*>>

}
