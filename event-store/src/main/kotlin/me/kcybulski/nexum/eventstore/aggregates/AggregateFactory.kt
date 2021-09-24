package me.kcybulski.nexum.eventstore.aggregates

interface AggregateFactory<A : AggregateRoot<A>, T : Any> {

    fun onCreate(event: T): A

}
