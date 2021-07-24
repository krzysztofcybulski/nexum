package me.kcybulski.nexum.eventstore

data class PublishEventConfiguration(
    val errorHandler: (error: PublishingError) -> Unit
)

class PublishEventConfigurationBuilder {

    var errorHandler: (error: PublishingError) -> Unit = {}

    fun onError(handler: (error: PublishingError) -> Unit) {
        this.errorHandler = handler
    }

    fun build() = PublishEventConfiguration(
        errorHandler = errorHandler
    )
}
