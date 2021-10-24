plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version "1.5.21"
}

group = "me.kcybulski.nexum"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.4.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.4.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "me.kcybulski.nexum"
            artifactId = "event-store"
            version = "1.4.0"
            from(components["java"])
            pom {
                name.set("Nexum")
                description.set("Kotlin Events Store")
                url.set("https://kcybulski.me/")
                developers {
                    developer {
                        id.set("krzysztofcybulski")
                        name.set("Krzysztof Cybulski")
                        email.set("krzysztofpcy@gmail.com")
                    }
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                    scm {
                        connection.set("scm:git:git@github.com:krzysztofcybulski/nexum.git")
                        developerConnection.set("scm:git:git@github.com:krzysztofcybulski/nexum.git")
                        url.set("https://github.com/krzysztofcybulski/nexum/")
                    }
                }
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    sign(publishing.publications["mavenJava"])
}
