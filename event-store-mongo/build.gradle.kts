plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version "1.5.30-M1"
}

group = "me.kcybulski.nexum"
version = "1.0.0"

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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "me.kcybulski.nexum"
            artifactId = "event-store-mongo"
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
