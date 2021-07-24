package me.kcybulski.nexum.eventstore.aggregates

interface AggregateRoot<A: AggregateRoot<A>> {

    val aggregatesHolder: AggregatesHolder

    fun <T> apply(event: T): A

    fun <T> event(event: T) {
        aggregatesHolder.addEvent(this, event)
        apply(event)
    }

    fun store(stream: String) {
        aggregatesHolder.store(this, stream)
    }
}
