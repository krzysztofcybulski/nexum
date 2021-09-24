package me.kcybulski.nexum.eventstore.aggregates

import kotlin.reflect.KClass

interface FactoriesRegistry {

    fun <T : AggregateRoot<T>, E : Any> save(eventClass: KClass<E>, factory: AggregateFactory<T, E>)
    operator fun <T : AggregateRoot<T>, E : Any> get(eventClass: KClass<E>): AggregateFactory<T, E>?


}
