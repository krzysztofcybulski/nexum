package me.kcybulski.nexum.eventstore.data

import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot

data class OrderAggregate(
    val products: List<String> = emptyList()
) : AggregateRoot<OrderAggregate>() {

    fun addProduct(product: String) {
        event(ProductAddedEvent(product))
    }

    override fun <T> apply(event: T): OrderAggregate =
        when (event) {
            is ProductAddedEvent -> copy(products = products + event.name)
            else -> this
        }
}
