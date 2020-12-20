import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") apply false
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
}

group = "com.github.al.mfs"
version = "0.1.0"

allprojects {
    repositories {
        mavenCentral()
    }

    configurations.all {
        resolutionStrategy {
            dependencySubstitution {
                substitute(module("commons-logging:commons-logging"))
                    .with(module("org.slf4j:jcl-over-slf4j:1.7.30"))
            }
        }
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }
    }
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            target("**/*.kt")
            ktlint(versions.ktlint)
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

tasks {
    dependencyUpdates {
        checkConstraints = true
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                        .any { it.matches(candidate.version) }
                    if (rejected) {
                        reject("Release candidate")
                    }
                }
            }
        }
    }
}
