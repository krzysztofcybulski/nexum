plugins {
    kotlin("jvm") version "1.5.30-M1"
}

group = "me.kcybulski.nexum"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":event-store"))
    implementation(kotlin("stdlib"))
    implementation("org.litote.kmongo:kmongo:4.2.8")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.4.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.4.3")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.0.0")
    testImplementation("org.testcontainers:mongodb:1.15.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
