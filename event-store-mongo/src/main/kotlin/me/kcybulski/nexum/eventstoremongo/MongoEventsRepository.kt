package me.kcybulski.nexum.eventstoremongo

import com.mongodb.client.MongoCollection
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventsRepository
import me.kcybulski.nexum.eventstore.events.NoStream
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.eq

internal class MongoEventsRepository(
    private val collection: MongoCollection<MongoDomainEvent<*>>
) : EventsRepository {

    init {
        collection.ensureIndex(MongoDomainEvent<*>::stream)
    }

    override fun <T> save(event: DomainEvent<T>) {
        collection.insertOne(event.toMongo())
    }

    override fun loadStream(stream: StreamId): List<DomainEvent<*>> = collection
        .find(MongoDomainEvent<*>::stream eq stream.raw)
        .map(MongoDomainEvent<*>::toDomain)
        .toList()
}

private fun <T> DomainEvent<T>.toMongo() = MongoDomainEvent(
    payload = payload,
    stream = stream.toMongo(),
    timestamp = timestamp
)

private fun <T> MongoDomainEvent<T>.toDomain() = DomainEvent(
    payload = payload,
    stream = stream?.let(::StreamId) ?: NoStream,
    timestamp = timestamp
)

private fun Stream.toMongo() = when (this) {
    is StreamId -> raw
    is NoStream -> null
}
