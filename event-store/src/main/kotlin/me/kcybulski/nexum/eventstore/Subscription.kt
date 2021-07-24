package me.kcybulski.nexum.eventstore

interface Subscription<T> {
    fun unsubscribe()
}
