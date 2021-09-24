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

class AggregateV2Spec : BehaviorSpec({

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
})
