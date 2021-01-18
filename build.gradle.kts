import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") apply false
    id("com.github.ben-manes.versions")
    id("com.diffplug.spotless")
}

group = "com.github.al.mfs"
version = "0.1.0"

allprojects {
    repositories {
        mavenCentral()
    }

    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.apache.httpcomponents" && requested.name == "httpclient") {
                    useVersion("4.5.13")
                }
                if (requested.group == "org.apache.httpcomponents" && requested.name == "httpcore") {
                    useVersion("4.4.14")
                }
                if (requested.group == "org.slf4j") {
                    useVersion(versions.slf4j)
                }
            }
            dependencySubstitution {
                substitute(module("commons-logging:commons-logging"))
                    .with(module("org.slf4j:jcl-over-slf4j:${versions.slf4j}"))
            }
            exclude("org.yaml", "snakeyaml")
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
