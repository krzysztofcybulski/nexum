package me.kcybulski.nexum.assertions

import io.kotest.matchers.shouldBe

class EventsStreamAssert<T>(
    private val eventStream: Iterator<T>
) {

    fun hasEvent(verifier: (T) -> Boolean): EventsStreamAssert<T> {
        verifier(eventStream.next()) shouldBe true
        return this
    }

    fun andNoMore(): EventsStreamAssert<T> {
        eventStream.hasNext() shouldBe false
        return this
    }
}
