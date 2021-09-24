package me.kcybulski.nexum.eventstore.data

import me.kcybulski.nexum.eventstore.aggregates.AggregateFactory
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

class OrderAggregateFactory : AggregateFactory<OrderAggregate, OrderCreated> {
    override fun onCreate(event: OrderCreated): OrderAggregate = OrderAggregate(
        listOf(event.firstProduct)
    )
}

data class OrderCreated(val firstProduct: String)
