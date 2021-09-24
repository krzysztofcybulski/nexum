package me.kcybulski.nexum.eventstore.events

import java.time.Clock
import java.util.UUID.randomUUID

class EventsFactory(
    private val clock: Clock = Clock.systemUTC()
) {

    fun <T : Any> create(payload: T, stream: Stream = NoStream): DomainEvent<T> = DomainEvent(
        id = EventId(randomUUID().toString()),
        payload = payload,
        stream = stream,
        timestamp = clock.instant()
    )

}
