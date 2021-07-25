package me.kcybulski.nexum.eventstore.events

interface EventsRepository {

    fun <T> save(event: DomainEvent<T>)
    fun loadStream(stream: StreamId): List<DomainEvent<*>>

}
