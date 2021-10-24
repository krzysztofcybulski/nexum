package me.kcybulski.nexum.eventstore.data

import me.kcybulski.nexum.eventstore.aggregates.AggregateFactory
import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot

class OrderAggregate(
    val products: List<String> = emptyList()
) : AggregateRoot<OrderAggregate>() {

    fun addProduct(product: String) = event(ProductAddedEvent(product))

    fun addProducts(vararg products: String) = products.fold(this) { agg, product -> agg.event(ProductAddedEvent(product)) }

    override fun <T> applyEvent(event: T): OrderAggregate =
        when (event) {
            is ProductAddedEvent -> OrderAggregate(products = products + event.name)
            else -> this
        }
}

class OrderAggregateFactory : AggregateFactory<OrderAggregate, OrderCreated> {
    override fun onCreate(event: OrderCreated): OrderAggregate = OrderAggregate(
        listOf(event.firstProduct)
    )
}

data class OrderCreated(val firstProduct: String)
