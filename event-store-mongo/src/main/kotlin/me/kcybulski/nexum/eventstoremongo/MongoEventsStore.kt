package me.kcybulski.nexum.eventstoremongo

import me.kcybulski.nexum.eventstore.events.EventsRepository

fun mongoEventsRepository(configuration: MongoEventsRepositoryBuilder.() -> Unit = {}): EventsRepository =
    MongoEventsRepositoryBuilder().also(configuration).build()
