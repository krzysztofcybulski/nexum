package me.kcybulski.nexum.eventstoremongo

import org.litote.kmongo.KMongo.createClient

class MongoEventsRepositoryBuilder {

    lateinit var connectionString: String
    lateinit var database: String
    var collection: String = "events"

    internal fun build() = createClient(connectionString)
        .getDatabase(database)
        .getCollection(collection, MongoDomainEvent::class.java)
        .let(::MongoEventsRepository)

}
