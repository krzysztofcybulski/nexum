package me.kcybulski.nexum.eventstore

sealed class PublishingError

class PublishingUncheckedException(val exception: RuntimeException) : PublishingError()
