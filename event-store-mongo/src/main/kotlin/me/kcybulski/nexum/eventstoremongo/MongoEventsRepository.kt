package me.kcybulski.nexum.eventstoremongo

import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import me.kcybulski.nexum.eventstore.events.DomainEvent
import me.kcybulski.nexum.eventstore.events.EventId
import me.kcybulski.nexum.eventstore.events.EventStreamDirection.BACKWARD
import me.kcybulski.nexum.eventstore.events.EventStreamDirection.FORWARD
import me.kcybulski.nexum.eventstore.events.EventsQuery
import me.kcybulski.nexum.eventstore.events.EventsRepository
import me.kcybulski.nexum.eventstore.events.NoStream
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId
import org.litote.kmongo.`in`
import org.litote.kmongo.ascendingSort
import org.litote.kmongo.descendingSort
import org.litote.kmongo.ensureIndex
import java.util.stream.Stream as JavaStream

internal class MongoEventsRepository(
    private val collection: MongoCollection<MongoDomainEvent<*>>
) : EventsRepository {

    init {
        collection.ensureIndex(MongoDomainEvent<*>::stream)
    }

    override fun <T : Any> save(event: DomainEvent<T>) {
        collection.insertOne(event.toMongo())
    }

    override fun query(query: EventsQuery): JavaStream<DomainEvent<*>> = collection
        .find(MongoDomainEvent<*>::stream `in` query.streamNames())
        .ifQuery(query.direction == FORWARD) { it.ascendingSort(MongoDomainEvent<*>::timestamp) }
        .ifQuery(query.direction == BACKWARD) { it.descendingSort(MongoDomainEvent<*>::timestamp) }
        .ifQuery(query.limit != null) { it.limit(query.limit!!) }
        .map(MongoDomainEvent<*>::toDomain)
        .toList()
        .stream()
}

private fun <T : Any> DomainEvent<T>.toMongo() = MongoDomainEvent(
    id = id.raw,
    payload = payload,
    stream = stream.toMongo(),
    timestamp = timestamp
)

private fun <T : Any> MongoDomainEvent<T>.toDomain() = DomainEvent(
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

private fun <E : MongoDomainEvent<*>> FindIterable<E>.ifQuery(
    condition: Boolean,
    handler: (FindIterable<E>) -> FindIterable<E>
) =
    if (condition) handler(this) else this
