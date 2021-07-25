package me.kcybulski.nexum.eventstore.events

class EventsFacade(
    private val eventsRepository: EventsRepository,
    private val eventsFactory: EventsFactory
) {

    fun <T> save(payload: T, stream: Stream) = eventsFactory
        .create(payload, stream)
        .let(eventsRepository::save)

    fun loadStream(stream: StreamId): List<DomainEvent<*>> = eventsRepository.loadStream(stream)

}
