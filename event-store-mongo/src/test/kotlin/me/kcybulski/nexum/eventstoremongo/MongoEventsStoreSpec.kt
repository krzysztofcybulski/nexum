package me.kcybulski.nexum.eventstoremongo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventId
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.reader.EventsQuery.Companion.query
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName.parse
import java.time.Instant.now
import kotlin.streams.toList

class MongoEventsStoreSpec : BehaviorSpec({

    val mongoDB = MongoDBContainer(parse("mongo:4.0.10"))
    listener(mongoDB.perSpec())

    given("Mongo events repository") {
        val repository = mongoEventsRepository {
            connectionString = mongoDB.replicaSetUrl
            database = "test"
        }
        val streamId = StreamId("1")
        `when`("Event is saved") {
            repository.save(DomainEvent(EventId("1"), "Hello", streamId, now()))
            then("Event should be in loaded stream") {
                repository
                    .query(query { stream(streamId) })
                    .map(DomainEvent<*>::payload)
                    .toList() shouldBe listOf("Hello")
            }
        }
    }
})
