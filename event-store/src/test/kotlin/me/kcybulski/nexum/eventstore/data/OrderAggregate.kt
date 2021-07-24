package me.kcybulski.nexum.eventstore.data

import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.AggregateRoot

class OrderAggregate(
    eventStore: EventStore,
    val products: MutableList<String> = mutableListOf()
) : AggregateRoot(eventStore) {

    fun addProduct(product: String) {
        event(ProductAddedEvent(product))
    }

    override fun <T> apply(event: T) {
        when (event) {
            is ProductAddedEvent -> products += event.name
        }
    }

}
