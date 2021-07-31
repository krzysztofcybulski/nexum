package me.kcybulski.nexum.eventstore.publishing

data class PublishEventConfiguration(
    val errorHandler: (error: PublishingError) -> Unit
) {

    companion object {
        fun publishConfiguration(configuration: PublishEventConfigurationBuilder.() -> Unit = {}) =
            PublishEventConfigurationBuilder()
                .apply(configuration)
                .build()
    }
}

class PublishEventConfigurationBuilder {

    var errorHandler: (error: PublishingError) -> Unit = {}

    fun onError(handler: (error: PublishingError) -> Unit) {
        this.errorHandler = handler
    }

    fun build() = PublishEventConfiguration(
        errorHandler = errorHandler
    )
}
