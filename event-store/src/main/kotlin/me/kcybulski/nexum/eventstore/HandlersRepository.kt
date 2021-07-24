package me.kcybulski.nexum.eventstore

interface HandlersRepository {

    fun <T> findHandlers(event: T): List<(T) -> Unit>
    fun <T> register(event: Class<out T>, handler: (T) -> Unit)
    fun <T> unregister(event: Class<out T>, handler: (T) -> Unit)
    fun unregisterAll()

}
