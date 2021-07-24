package me.kcybulski.nexum.eventstore

interface AggregatesHolder {

    fun <T : AggregateRoot<*>, E> addEvent(aggregate: T, event: E)
    fun <T : AggregateRoot<*>> store(aggregate: T, stream: String): T

}

data class EventToPersist<T>(val payload: T)
