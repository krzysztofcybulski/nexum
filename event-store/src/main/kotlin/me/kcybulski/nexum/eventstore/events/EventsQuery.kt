package me.kcybulski.nexum.eventstore.events

import me.kcybulski.nexum.eventstore.events.EventStreamDirection.BACKWARD
import me.kcybulski.nexum.eventstore.events.EventStreamDirection.FORWARD

data class EventsQuery(
    val streams: Set<StreamId>,
    val limit: Int? = null,
    val direction: EventStreamDirection
) {

    companion object {
        fun query(configuration: EventsQueryBuilder.() -> Unit = {}) =
            EventsQueryBuilder()
                .apply(configuration)
                .build()
    }
}

class EventsQueryBuilder {

    private val streams: MutableSet<StreamId> = mutableSetOf()
    var limit: Int? = null
    private var direction: EventStreamDirection = FORWARD

    fun stream(streamId: StreamId) {
        streams += streamId
    }

    fun forward() {
        direction = FORWARD
    }

    fun backward() {
        direction = BACKWARD
    }

    fun build() = EventsQuery(
        streams = streams.toSet(),
        limit = limit,
        direction = direction
    )
}

enum class EventStreamDirection {

    FORWARD, BACKWARD

}
