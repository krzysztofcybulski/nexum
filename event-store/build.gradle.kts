plugins {
    kotlin("jvm") version "1.5.0-RC"
    groovy
}

group = "me.kcybulski"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("org.codehaus.groovy:groovy-all:3.0.7")
    testImplementation("org.spockframework:spock-core:2.0-M5-groovy-3.0")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.4.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.4.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}