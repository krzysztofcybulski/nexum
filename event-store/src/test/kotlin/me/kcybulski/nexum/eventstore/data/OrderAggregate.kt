package me.kcybulski.nexum.eventstore.data

import me.kcybulski.nexum.eventstore.AggregateRoot
import me.kcybulski.nexum.eventstore.AggregatesHolder

data class OrderAggregate(
    override val aggregatesHolder: AggregatesHolder,
    val products: List<String> = emptyList()
) : AggregateRoot<OrderAggregate> {

    fun addProduct(product: String) {
        event(ProductAddedEvent(product))
    }

    override fun <T> apply(event: T): OrderAggregate =
        when (event) {
            is ProductAddedEvent -> copy(products = products + event.name)
            else -> this
        }
}
