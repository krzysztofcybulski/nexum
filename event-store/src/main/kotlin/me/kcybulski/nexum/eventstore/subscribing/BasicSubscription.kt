package me.kcybulski.nexum.eventstore.subscribing

internal class BasicSubscription<T>(
    private val handler: EventHandler<T>,
    private val unsubscribe: (EventHandler<T>) -> Unit
) : Subscription<T> {
    override fun unsubscribe() {
        unsubscribe(handler)
    }
}
