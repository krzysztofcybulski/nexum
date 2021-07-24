package me.kcybulski.nexum.spec

import io.kotest.core.spec.style.BehaviorSpec
import me.kcybulski.nexum.TestSubscriber
import me.kcybulski.nexum.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.DomainEvent
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

class EventPublishingSpec : BehaviorSpec({

    val testSubscriber = TestSubscriber()
    val eventStore: EventStore = InMemoryEventStore.create()

    eventStore.subscribe(ProductAddedEvent::class.java, testSubscriber::onEvent)

    afterTest {
        testSubscriber.reset()
    }

    given("An event") {
        val event = ProductAddedEvent("Milk")
        `when`("Event is published") {
            eventStore.publish(event)
            then("Subscriber has been called") {
                testSubscriber
                    .assertStream(ProductAddedEvent::class.java)
                    .hasEvent { it.name == "Milk" }
                    .andNoMore()
            }
        }
    }
})
