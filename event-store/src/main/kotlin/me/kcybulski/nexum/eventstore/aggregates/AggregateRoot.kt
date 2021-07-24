package me.kcybulski.nexum.eventstore.aggregates

import me.kcybulski.nexum.eventstore.Stream
import me.kcybulski.nexum.eventstore.StreamId

interface AggregateRoot<A: AggregateRoot<A>> {

    val aggregatesHolder: AggregatesHolder

    fun <T> apply(event: T): A

    fun <T> event(event: T) {
        aggregatesHolder.addEvent(this, event)
        apply(event)
    }

    fun store(stream: Stream) {
        aggregatesHolder.store(this, stream)
    }
}
