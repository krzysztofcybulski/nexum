package me.kcybulski.nexum.eventstore.subscribing

interface Subscription<T> {
    fun unsubscribe()
}
