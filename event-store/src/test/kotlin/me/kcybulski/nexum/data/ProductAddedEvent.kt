package me.kcybulski.nexum.data

import me.kcybulski.nexum.eventstore.DomainEvent

data class ProductAddedEvent(val name: String)