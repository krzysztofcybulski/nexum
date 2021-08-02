package me.kcybulski.nexum.eventstore.spec

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeIn
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.TestSubscriber
import me.kcybulski.nexum.eventstore.data.OrderAggregate
import me.kcybulski.nexum.eventstore.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

class AggregateSpec : BehaviorSpec({

    val testSubscriber = TestSubscriber()
    val eventStore: EventStore = InMemoryEventStore.create()

    eventStore.subscribe(ProductAddedEvent::class, testSubscriber::onEvent)

    afterTest {
        testSubscriber.reset()
    }

    given("Stored order with milk") {
        val stream = StreamId("order-with-milk")
        OrderAggregate()
            .also { it.addProduct("Milk") }
            .also { eventStore.store(it, stream) }
        `when`("Loaded order") {
            val orderWithMilk = eventStore.load(stream, ::OrderAggregate)
            then("Milk has been added") {
                "Milk" shouldBeIn orderWithMilk.products
            }
        }
    }

    given("Stored order with apple") {
        val stream = StreamId("order-with-apple")
        eventStore.with(stream, ::OrderAggregate) {
            addProduct("Apple")
        }
        `when`("Loaded order") {
            val orderWithApple = eventStore.load(stream, ::OrderAggregate)
            then("Apple has been added") {
                "Apple" shouldBeIn orderWithApple.products
            }
        }
    }
})
