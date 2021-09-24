package me.kcybulski.nexum.eventstore.handlers

import me.kcybulski.nexum.eventstore.subscribing.EventHandler
import kotlin.reflect.KClass

interface HandlersRegistry {

    fun <T : Any> findHandlers(event: KClass<T>): List<suspend (T: Any) -> Unit>
    fun <T> register(handler: EventHandler<T>): EventHandler<T>
    fun <T> unregister(handler: EventHandler<T>)
    fun unregisterAll()

}
