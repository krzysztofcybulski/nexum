package me.kcybulski.nexum.eventstoremongo

import java.time.Instant

internal data class MongoDomainEvent<T : Any>(
    val id: String,
    val payload: T,
    val stream: String?,
    val timestamp: Instant
)
