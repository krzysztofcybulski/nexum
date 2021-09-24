package me.kcybulski.nexum.eventstore.spec

import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.delay
import me.kcybulski.nexum.eventstore.TestSubscriber
import me.kcybulski.nexum.eventstore.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class EventAsyncSubscribingSpec : BehaviorSpec({
    val testSubscriber = TestSubscriber()
    val eventStore = InMemoryEventStore.create()

    afterTest {
        testSubscriber.reset()
    }

    given("Async event subscriptions") {
        eventStore.subscribe(ShoppingListAdded::class) {
            delay(10)
            testSubscriber.onEvent(ProductAddedEvent("Milk"))
        }
        eventStore.subscribe(ShoppingListAdded::class) {
            testSubscriber.onEvent(ProductAddedEvent("Bread"))
        }
        `when`("Event is published synchronously") {
            eventStore.publishAsync(ShoppingListAdded)
            then("Subscriber has been called in parallel") {
                testSubscriber
                    .assertStream(ProductAddedEvent::class.java)
                    .hasEvent { it.name == "Bread" }
                    .hasEvent { it.name == "Milk" }
                    .andNoMore()
            }
        }
    }
})

object ShoppingListAdded
