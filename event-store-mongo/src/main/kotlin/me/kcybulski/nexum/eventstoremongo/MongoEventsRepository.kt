package me.kcybulski.nexum.eventstoremongo

import com.mongodb.client.MongoCollection
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventId
import me.kcybulski.nexum.eventstore.events.EventsRepository
import me.kcybulski.nexum.eventstore.events.NoStream
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.reader.EventsQuery
import org.litote.kmongo.`in`
import org.litote.kmongo.ascendingSort
import org.litote.kmongo.ensureIndex
import java.util.stream.Stream as JavaStream

internal class MongoEventsRepository(
    private val collection: MongoCollection<MongoDomainEvent<*>>
) : EventsRepository {

    init {
        collection.ensureIndex(MongoDomainEvent<*>::stream)
    }

    override fun <T> save(event: DomainEvent<T>) {
        collection.insertOne(event.toMongo())
    }

    override fun query(query: EventsQuery): JavaStream<DomainEvent<*>> = collection
        .find(MongoDomainEvent<*>::stream `in` query.streamNames())
        .ascendingSort(MongoDomainEvent<*>::timestamp)
        .map(MongoDomainEvent<*>::toDomain)
        .toList()
        .stream()
}

private fun <T> DomainEvent<T>.toMongo() = MongoDomainEvent(
    id = id.raw,
    payload = payload,
    stream = stream.toMongo(),
    timestamp = timestamp
)

private fun <T> MongoDomainEvent<T>.toDomain() = DomainEvent(
    id = EventId(id),
    payload = payload,
    stream = stream?.let(::StreamId) ?: NoStream,
    timestamp = timestamp
)

private fun Stream.toMongo() = when (this) {
    is StreamId -> raw
    is NoStream -> null
}

private fun EventsQuery.streamNames() = streams.map(StreamId::raw)
