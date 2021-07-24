package me.kcybulski.nexum.eventstore.events

import java.time.Clock

class EventsFactory(
    private val clock: Clock = Clock.systemUTC()
) {

    fun <T> create(payload: T, stream: Stream = NoStream): DomainEvent<T> = DomainEvent(
        payload = payload,
        stream = stream,
        timestamp = clock.instant()
    )

}
