package me.kcybulski.nexum.eventstoremongo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.StreamId
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName.parse
import java.time.Instant.now

class MongoEventsStoreSpec : BehaviorSpec({

    val mongoDB = MongoDBContainer(parse("mongo:4.0.10"))
    listener(mongoDB.perSpec())

    given("Mongo events repository") {
        val repository = mongoEventsRepository {
            connectionString = mongoDB.replicaSetUrl
            database = "test"
        }
        val stream = StreamId("1")
        `when`("Event is saved") {
            repository.save(DomainEvent("Hello", stream, now()))
            then("Event should be in loaded stream") {
                repository.loadStream(stream).map(DomainEvent<*>::payload) shouldBe listOf("Hello")
            }
        }
    }
})
