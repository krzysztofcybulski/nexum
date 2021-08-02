package me.kcybulski.nexum.eventstore.aggregates

abstract class AggregateRoot<A : AggregateRoot<A>>(
    private val strategy: AggregateStrategy = ManuallyStoreEvents
) {

    internal val unpublishedEvents: MutableList<Any> = mutableListOf()

    abstract fun <T> apply(event: T): A

    fun <T> event(event: T) {
        unpublishedEvents.add(event as Any)
        apply(event)
    }
}
