package me.kcybulski.nexum.eventstore

class DomainEvent<T>(val payload: T, val stream: String)
