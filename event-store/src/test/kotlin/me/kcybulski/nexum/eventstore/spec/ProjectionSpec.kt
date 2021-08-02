package me.kcybulski.nexum.eventstore.spec

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

class ProjectionSpec : BehaviorSpec({

    val eventStore: EventStore = InMemoryEventStore.create()

    given("Some bank account with $100") {
        val streamId = StreamId("account-1")
        eventStore.publish(MoneyDeposited(100), streamId)
        `when`("Withdrawn $30") {
            eventStore.publish(MoneyWithdrawn(30), streamId)
            then("Account balance is $70") {
                eventStore.project(0, { stream(streamId) }) { balance, event ->
                    when (event) {
                        is MoneyDeposited -> balance + event.amount
                        is MoneyWithdrawn -> balance - event.amount
                        else -> balance
                    }
                } shouldBe 70
            }
        }
    }
})

data class MoneyDeposited(val amount: Int)
data class MoneyWithdrawn(val amount: Int)
