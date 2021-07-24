package me.kcybulski.nexum.eventstore.spec

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeIn
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.TestSubscriber
import me.kcybulski.nexum.eventstore.data.OrderAggregate
import me.kcybulski.nexum.eventstore.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

class AggregateSpec : BehaviorSpec({

    val testSubscriber = TestSubscriber()
    val eventStore: EventStore = InMemoryEventStore.create()

    eventStore.subscribe(ProductAddedEvent::class.java, testSubscriber::onEvent)

    afterTest {
        testSubscriber.reset()
    }

    given("Stored order with milk") {
        eventStore.new(::OrderAggregate)
            .also { it.addProduct("Milk") }
            .store("order-with-milk")
        `when`("Loaded order") {
            val orderWithMilk = eventStore.load("order-with-milk", ::OrderAggregate)
            then("Milk has been added") {
                "Milk" shouldBeIn orderWithMilk.products
            }
        }
    }
})
