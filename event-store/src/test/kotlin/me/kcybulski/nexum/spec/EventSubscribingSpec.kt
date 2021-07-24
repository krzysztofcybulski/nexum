package me.kcybulski.nexum.spec

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.nexum.TestSubscriber
import me.kcybulski.nexum.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

class EventSubscribingSpec : BehaviorSpec({
    val testSubscriber = TestSubscriber()
    val eventStore = InMemoryEventStore.create()

    afterTest {
        testSubscriber.reset()
        eventStore.unsubscribeAll()
    }

    given("Event subscription (1)") {
        eventStore.subscribe(ProductAddedEvent::class.java, testSubscriber::onEvent)
        `when`("Event is published") {
            eventStore.publish(ProductAddedEvent("Milk"))
            then("Subscriber has been called") {
                testSubscriber
                    .assertStream(ProductAddedEvent::class.java)
                    .hasEvent { it.name == "Milk" }
                    .andNoMore()
            }
        }
    }

    given("Event subscription (2)") {
        val subscription = eventStore.subscribe(ProductAddedEvent::class.java, testSubscriber::onEvent)
        `when`("Unsubscribed") {
            subscription.unsubscribe()
            and("Published event") {
                eventStore.publish(ProductAddedEvent("Milk"))
                then("Subscriber has not been called") {
                    testSubscriber
                        .assertStream(ProductAddedEvent::class.java)
                        .andNoMore()
                }
            }
        }
    }

    given("Event subscription (3)") {
        eventStore.subscribe(ProductAddedEvent::class.java) { throw RuntimeException() }
        `when`("Event is published") {
            eventStore.publish(ProductAddedEvent("Milk"))
            then("Error has been eaten") {
                testSubscriber
                    .assertStream(ProductAddedEvent::class.java)
                    .andNoMore()
            }
        }
    }

    given("Event subscription (4)") {
        eventStore.subscribe(ProductAddedEvent::class.java) { throw RuntimeException() }
        `when`("Event is published") {
            var errorMessage = "None"
            eventStore.publish(ProductAddedEvent("Milk")) {
                onError { errorMessage = "Milk not added" }
            }
            then("Event handler has been called") {
                errorMessage shouldBe "Milk not added"
            }
        }
    }
})
