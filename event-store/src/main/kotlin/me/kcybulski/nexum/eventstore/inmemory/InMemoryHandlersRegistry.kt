package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.handlers.HandlersRegistry
import me.kcybulski.nexum.eventstore.subscribing.EventHandler
import kotlin.reflect.KClass

internal class InMemoryHandlersRegistry : HandlersRegistry {

    private val handlers: MutableList<EventHandler<*>> = mutableListOf()

    override fun <T : Any> findHandlers(event: KClass<T>): List<suspend (T: Any) -> Unit> =
        handlers
            .filter { it.accepting(event) }
            .map { it.handler as (suspend (Any) -> Unit) }

    override fun <T> register(handler: EventHandler<T>): EventHandler<T> {
        handlers += handler
        return handler
    }

    override fun <T> unregister(handler: EventHandler<T>) {
        handlers -= handler
    }

    override fun unregisterAll() {
        handlers.clear()
    }
}
