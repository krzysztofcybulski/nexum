package me.kcybulski.nexum.eventstore.subscribing

import kotlin.reflect.KClass

sealed class EventHandler<T>(val handler: suspend (T) -> Unit) {

    abstract fun <A : Any> accepting(eventType: KClass<A>): Boolean

}

class EventTypeHandler<T : Any>(private val eventType: KClass<T>, handler: suspend (T) -> Unit) :
    EventHandler<T>(handler) {
    override fun <A : Any> accepting(eventType: KClass<A>): Boolean = eventType == this.eventType
}

class AllTypesHandler<T>(handler: suspend (T) -> Unit) : EventHandler<T>(handler) {
    override fun <A : Any> accepting(eventType: KClass<A>): Boolean = true
}
