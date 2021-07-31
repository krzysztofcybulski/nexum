package me.kcybulski.nexum.eventstore.spec

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.data.ProductAddedEvent
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import kotlin.streams.toList
import java.util.stream.Stream as JavaStream

class QuerySpec : BehaviorSpec({

    val eventStore: EventStore = InMemoryEventStore.create()

    given("Dad shopping list") {
        val streamId = StreamId("dad-shopping-list")
        eventStore.append(ProductAddedEvent("Milk"), streamId)
        eventStore.append(ProductAddedEvent("Egg"), streamId)
        eventStore.append(ProductAddedEvent("Water"), streamId)
        and("Mum shopping list") {
            eventStore.append(ProductAddedEvent("Milk"), StreamId("mum-shopping-list"))
            `when`("Mum shopping list is read") {
                val events = eventStore.read {
                    stream(streamId)
                }
                then("All mum products are present") {
                    events.productNames() shouldBe listOf("Milk", "Egg", "Water")
                }
            }
        }
    }
})

private fun JavaStream<DomainEvent<*>>.productNames() = map { it.payload as ProductAddedEvent }.map { it.name }.toList()
