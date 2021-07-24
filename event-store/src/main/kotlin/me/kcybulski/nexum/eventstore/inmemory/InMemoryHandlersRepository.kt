package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.handlers.HandlersRepository

internal class InMemoryHandlersRepository : HandlersRepository {

    private val handlers: MutableList<InMemoryHandler<*>> = mutableListOf()

    override fun <T> findHandlers(event: T): List<(T) -> Unit> =
        handlers
            .filter { it.eventType == event!!::class.java }
            .map { it.handler as (T) -> Unit }

    override fun <T> register(event: Class<out T>, handler: (T) -> Unit) {
        handlers += InMemoryHandler(event, handler)
    }

    override fun <T> unregister(event: Class<out T>, handler: (T) -> Unit) {
        handlers -= InMemoryHandler(event, handler)
    }

    override fun unregisterAll() {
        handlers.clear()
    }
}


data class InMemoryHandler<T>(val eventType: Class<T>, val handler: (T) -> Unit)
