package me.kcybulski.nexum.eventstore.inmemory

import me.kcybulski.nexum.eventstore.aggregates.AggregateFactory
import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot
import me.kcybulski.nexum.eventstore.aggregates.FactoriesRegistry
import kotlin.reflect.KClass

internal class InMemoryFactoriesRegistry : FactoriesRegistry {

    private val handlers: MutableMap<KClass<*>, AggregateFactory<*, *>> = mutableMapOf()

    override fun <T : AggregateRoot<T>, E : Any> save(eventClass: KClass<E>, factory: AggregateFactory<T, E>) {
        handlers[eventClass] = factory
    }

    override fun <T : AggregateRoot<T>, E : Any> get(eventClass: KClass<E>): AggregateFactory<T, E>? =
        handlers[eventClass]
                as AggregateFactory<T, E>?

}
