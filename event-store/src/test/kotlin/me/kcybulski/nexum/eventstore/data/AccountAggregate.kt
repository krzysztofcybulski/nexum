package me.kcybulski.nexum.eventstore.data

import me.kcybulski.nexum.eventstore.aggregates.AggregateRoot

data class AccountAggregate(
    val balance: Int
) : AggregateRoot<AccountAggregate>() {

    fun deposit(amount: Int) {
        event(MoneyDeposited(amount))
    }

    fun withdraw(amount: Int) {
        require(balance >= amount)
        event(MoneyWithdrawn(amount))
    }

    override fun <T> apply(event: T): AccountAggregate =
        when (event) {
            is MoneyDeposited -> copy(balance = balance + event.amount)
            is MoneyWithdrawn -> copy(balance = balance - event.amount)
            else -> this
        }
}
