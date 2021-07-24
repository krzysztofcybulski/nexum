package me.kcybulski.nexum.eventstore

import java.time.Instant

class DomainEvent<T>(val payload: T, val stream: String, val timestamp: Instant)
