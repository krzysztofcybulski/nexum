package me.kcybulski.nexum.eventstore.aggregates

abstract class AggregateRoot<A : AggregateRoot<A>> {

    internal val unpublishedEvents: MutableList<Any> = mutableListOf()

    abstract fun <T> applyEvent(event: T): A

    fun <T> event(event: T): A {
        unpublishedEvents.add(event as Any)
        return applyEvent(event).also {
            it.unpublishedEvents.addAll(unpublishedEvents)
        }
    }
}
