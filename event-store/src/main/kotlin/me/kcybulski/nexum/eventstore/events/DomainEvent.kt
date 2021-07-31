package me.kcybulski.nexum.eventstore.events

import java.time.Instant

class DomainEvent<T>(
    val id: EventId,
    val payload: T,
    val stream: Stream = NoStream,
    val timestamp: Instant
)

sealed class Stream

class StreamId(val raw: String) : Stream()
object NoStream : Stream()

data class EventId(val raw: String)
