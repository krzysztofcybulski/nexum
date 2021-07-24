package me.kcybulski.nexum.eventstore.aggregates

import me.kcybulski.nexum.eventstore.Stream

interface AggregatesHolder {

    fun <T : AggregateRoot<*>, E> addEvent(aggregate: T, event: E)
    fun <T : AggregateRoot<*>> store(aggregate: T, stream: Stream): T

}

data class EventToPersist<T>(val payload: T)
