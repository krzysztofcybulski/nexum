package me.kcybulski.nexum.eventstore

class EventStore(
    private val handlersRepository: HandlersRepository,
    private val eventsRepository: EventsRepository
) {
    fun <T> subscribe(event: Class<out T>, handler: (T) -> Unit): Subscription<T> {
        handlersRepository.register(event, handler)
        return BasicSubscription(event, handler, this)
    }

    fun <T> publish(event: T, configuration: PublishEventConfigurationBuilder.() -> Unit = {}) {
        val config = PublishEventConfigurationBuilder().also(configuration).build()
        handlersRepository.findHandlers(event)
            .forEach { handler -> event.tryOrElse(handler) { config.errorHandler(it) } }
    }

    fun <T> unsubscribe(event: Class<out T>, handler: (T) -> Unit) {
        handlersRepository.unregister(event, handler)
    }

    fun <T : AggregateRoot> store(aggregate: T, stream: String) {
        aggregate.events.forEach { eventsRepository.save(DomainEvent(it.payload, stream)) }
        aggregate.events.clear()
    }

    fun <T : AggregateRoot> load(stream: String, factory: (EventStore) -> T): T =
        factory(this).applyAllEvents(eventsRepository.loadStream(stream))

    internal fun unsubscribeAll() {
        handlersRepository.unregisterAll()
    }

}

private fun <T> T.tryOrElse(func: (T) -> Unit, errorHandler: (PublishingError) -> Unit) = try {
    func(this)
} catch (e: RuntimeException) {
    errorHandler(PublishingUncheckedException(e))
}

private fun <T : AggregateRoot> T.applyAllEvents(events: List<DomainEvent<*>>): T {
    events.forEach { apply(it.payload) }
    return this
}
