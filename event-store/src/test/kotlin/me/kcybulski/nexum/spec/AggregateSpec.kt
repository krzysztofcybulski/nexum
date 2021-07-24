package me.kcybulski.nexum.spec

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeIn
import me.kcybulski.nexum.TestSubscriber
import me.kcybulski.nexum.data.OrderAggregate
import me.kcybulski.nexum.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

class AggregateSpec : BehaviorSpec({

    val testSubscriber = TestSubscriber()
    val eventStore: EventStore = InMemoryEventStore.create()

    eventStore.subscribe(ProductAddedEvent::class.java, testSubscriber::onEvent)

    afterTest {
        testSubscriber.reset()
    }

    given("New order") {
        val order = OrderAggregate(eventStore)
        `when`("Added milk product") {
            order.addProduct("Milk")
            then("Milk has been added") {
                "Milk" shouldBeIn order.products
            }
        }
    }

    given("Stored order with milk") {
        OrderAggregate(eventStore)
            .also { it.addProduct("Milk") }
            .store("order-with-milk")
        `when`("Loaded order") {
            val orderWithMilk = eventStore.load("order-with-milk") { OrderAggregate(it) }
            then("Milk has been added") {
                "Milk" shouldBeIn orderWithMilk.products
            }
        }
    }
})
