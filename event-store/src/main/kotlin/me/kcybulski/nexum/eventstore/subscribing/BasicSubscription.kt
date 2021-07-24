package me.kcybulski.nexum.eventstore.subscribing

import me.kcybulski.nexum.eventstore.EventStore

internal class BasicSubscription<T>(
    private val handler: EventHandler<T>,
    private val eventStore: EventStore
) : Subscription<T> {
    override fun unsubscribe() {
        eventStore.unsubscribe(handler)
    }
}
