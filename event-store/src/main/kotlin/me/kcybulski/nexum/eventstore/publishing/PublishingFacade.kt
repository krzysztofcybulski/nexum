package me.kcybulski.nexum.eventstore.publishing

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.kcybulski.nexum.eventstore.events.EventsFacade
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.handlers.HandlersRegistry

class PublishingFacade(
    private val handlersRegistry: HandlersRegistry,
    private val eventsFacade: EventsFacade
) {

    suspend fun <T : Any> publishAsync(
        event: T,
        stream: Stream,
        configuration: PublishEventConfiguration
    ) {
        append(event, stream)
        fireEventHandlers(event, configuration)
    }

    fun <T : Any> publish(
        event: T,
        stream: Stream,
        configuration: PublishEventConfiguration
    ) = runBlocking { publishAsync(event, stream, configuration) }

    fun <T : Any> append(event: T, stream: Stream) {
        eventsFacade.save(event, stream)
    }

    private suspend fun <T : Any> fireEventHandlers(event: T, configuration: PublishEventConfiguration) =
        coroutineScope {
            handlersRegistry
                .findHandlers(event::class)
                .forEach { handler -> launch { event.tryOrElse(handler) { configuration.errorHandler(it) } } }
        }

}

private suspend fun <T : Any> T.tryOrElse(func: suspend (T) -> Unit, errorHandler: (PublishingError) -> Unit) = try {
    func(this)
} catch (e: RuntimeException) {
    errorHandler(PublishingUncheckedException(e))
}
