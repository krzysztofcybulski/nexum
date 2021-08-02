package me.kcybulski.nexum.eventstore.aggregates

sealed class AggregateStrategy

object AutoStoreEvents : AggregateStrategy()
object ManuallyStoreEvents : AggregateStrategy()
