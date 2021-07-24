package me.kcybulski.nexum.eventstore.publishing

sealed class PublishingError

class PublishingUncheckedException(val exception: RuntimeException) : PublishingError()
