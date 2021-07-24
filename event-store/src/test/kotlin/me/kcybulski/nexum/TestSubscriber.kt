package me.kcybulski.nexum

import me.kcybulski.nexum.assertions.EventsStreamAssert

class TestSubscriber {

    private val eventsStream: MutableMap<Class<*>, MutableList<*>> = mutableMapOf()

    fun <T> onEvent(event: T) {
        eventsStream.merge(event!!::class.java, mutableListOf(event)) { a, b -> (a + b).toMutableList() }
    }

    fun reset() {
        eventsStream.clear()
    }

    fun <T> assertStream(clazz: Class<T>): EventsStreamAssert<T> {
        val eventsStream: List<T> = (eventsStream[clazz] ?: emptyList()) as List<T>
        return EventsStreamAssert(eventStream = eventsStream.iterator())
    }
}
