package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.handlers.HandlersRepository
import me.kcybulski.nexum.eventstore.subscribing.AllTypesHandler
import me.kcybulski.nexum.eventstore.subscribing.EventHandler
import me.kcybulski.nexum.eventstore.subscribing.EventTypeHandler
import kotlin.reflect.KClass

internal class InMemoryHandlersRepository : HandlersRepository {

    private val handlers: MutableList<EventHandler<*>> = mutableListOf()

    override fun <T: Any> findHandlers(event: KClass<T>): List<(T: Any) -> Unit> =
        handlers
            .filter { it.accepting(event) }
            .map { it.handler as (Any) -> Unit }

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
