# Nexum Event Store

![CI](https://github.com/krzysztofcybulski/nexum/actions/workflows/test.yml/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.kcybulski.nexum/event-store/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/me.kcybulski.nexum/event-store)

The open-source implementation of an Event Store for Kotlin.

Nexum Event Store is a library for publishing, consuming, storing and retrieving events.

Highly inspired by [Rails Event Store](https://railseventstore.org/) ✌️

# What is it for?

* Simple publish-subscribe bus
* Decouple business logic from external concerns
* Communication layer between loosely coupled components
* Extract side-effects (notifications, metrics etc.) from business code
* Build and audit-log
* Create read-models
* Implement event-sourcing
