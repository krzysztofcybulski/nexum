package me.kcybulski.nexum.eventstore.subscribing

import me.kcybulski.nexum.eventstore.EventStore

internal class BasicSubscription<T>(
    private val event: Class<out T>,
    private val handler: (T) -> Unit,
    private val eventStore: EventStore
) : Subscription<T> {
    override fun unsubscribe() {
        eventStore.unsubscribe(event, handler)
    }
}
