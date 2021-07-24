package me.kcybulski.nexum.eventstore

class EventStore(
    private val handlersRepository: HandlersRepository,
    private val eventsRepository: EventsRepository,
    private val aggregatesHolder: AggregatesHolder
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

    fun <T : AggregateRoot<T>> load(stream: String, factory: (AggregatesHolder) -> T): T =
        factory(aggregatesHolder).applyAllEvents(eventsRepository.loadStream(stream))

    fun <T : AggregateRoot<T>> new(factory: (AggregatesHolder) -> T): T = factory(aggregatesHolder)

    internal fun unsubscribeAll() {
        handlersRepository.unregisterAll()
    }

}

private fun <T> T.tryOrElse(func: (T) -> Unit, errorHandler: (PublishingError) -> Unit) = try {
    func(this)
} catch (e: RuntimeException) {
    errorHandler(PublishingUncheckedException(e))
}

private fun <T : AggregateRoot<T>> T.applyAllEvents(events: List<DomainEvent<*>>): T =
    events.fold(this) { agg, event -> agg.apply(event.payload) }
