package me.kcybulski.nexum.eventstore.spec

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeIn
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.TestSubscriber
import me.kcybulski.nexum.eventstore.data.OrderAggregate
import me.kcybulski.nexum.eventstore.data.OrderAggregateFactory
import me.kcybulski.nexum.eventstore.data.OrderCreated
import me.kcybulski.nexum.eventstore.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

class AggregateSpec : BehaviorSpec({

    val testSubscriber = TestSubscriber()
    val eventStore: EventStore = InMemoryEventStore.create()

    eventStore.subscribe(ProductAddedEvent::class, testSubscriber::onEvent)
    eventStore.register(OrderAggregateFactory())

    afterTest {
        testSubscriber.reset()
    }

    given("Created order with milk") {
        val stream = StreamId("order-with-milk")
        val order = eventStore
            .new<OrderAggregate, OrderCreated>(OrderCreated("Milk"))!!
        eventStore.store(order, stream)
        `when`("Loaded order") {
            val orderWithMilk = eventStore.load<OrderAggregate, OrderCreated>(stream)!!
            then("Milk has been added") {
                "Milk" shouldBeIn orderWithMilk.products
            }
        }
    }

    given("Stored order with apple and milk") {
        val stream = StreamId("order-with-apple-and-milk")
        eventStore.with<OrderAggregate, OrderCreated>(stream, OrderCreated("Milk")) {
            addProduct("Apple")
        }
        `when`("Loaded order") {
            val orderWithApple = eventStore.load<OrderAggregate, OrderCreated>(stream)!!
            then("Order contains apple and milk") {
                "Apple" shouldBeIn orderWithApple.products
                "Milk" shouldBeIn orderWithApple.products
            }
        }
    }

    given("Stored order with apple") {
        val stream = StreamId("order-with-apple")
        eventStore
            .new<OrderAggregate, OrderCreated>(OrderCreated("Apple"))
            ?.let { eventStore.store(it, stream) }
        `when`("Added milk") {
            eventStore.with<OrderAggregate, OrderCreated>(stream) {
                addProduct("Milk")
            }
            then("Milk has been added") {
                val order = eventStore.load<OrderAggregate, OrderCreated>(stream)!!
                "Apple" shouldBeIn order.products
                "Milk" shouldBeIn order.products
            }
        }
    }
})
