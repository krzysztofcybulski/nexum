package me.kcybulski.nexum.eventstore.subscribing

import me.kcybulski.nexum.eventstore.handlers.HandlersRegistry
import kotlin.reflect.KClass

class SubscriptionFacade(
    private val handlersRegistry: HandlersRegistry
) {

    fun <T : Any> subscribe(event: KClass<T>, handler: suspend (T) -> Unit): Subscription<T> =
        EventTypeHandler(event, handler)
            .let(handlersRegistry::register)
            .let { BasicSubscription(it, this::unsubscribe) }

    fun subscribeAll(handler: suspend (Any) -> Unit): Subscription<Any> = AllTypesHandler(handler)
        .let(handlersRegistry::register)
        .let { BasicSubscription(it, this::unsubscribe) }

    fun <T> unsubscribe(handler: EventHandler<T>) {
        handlersRegistry.unregister(handler)
    }

}
