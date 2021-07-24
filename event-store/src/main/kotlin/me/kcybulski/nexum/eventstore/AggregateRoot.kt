package me.kcybulski.nexum.eventstore

abstract class AggregateRoot(private val eventStore: EventStore) {
    internal val events: MutableList<EventToPersist<*>> = mutableListOf()

    fun <T> event(event: T) {
        events.add(EventToPersist(event))
        apply(event)
    }

    abstract fun <T> apply(event: T)

    fun store(stream: String) {
        eventStore.store(this, stream)
    }

}

data class EventToPersist<T>(val payload: T)
