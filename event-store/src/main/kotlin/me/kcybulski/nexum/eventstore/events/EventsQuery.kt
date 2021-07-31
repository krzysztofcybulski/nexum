package me.kcybulski.nexum.eventstore.reader

import me.kcybulski.nexum.eventstore.events.StreamId

data class EventsQuery(
    val streams: Set<StreamId>
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

    fun stream(streamId: StreamId) {
        streams += streamId
    }

    fun build() = EventsQuery(
        streams = streams.toSet()
    )
}
