package me.kcybulski.nexum.eventstore

interface EventsRepository {

    fun <T> save(event: DomainEvent<T>)
    fun loadStream(stream: String): List<DomainEvent<*>>

}
