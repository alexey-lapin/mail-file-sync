import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") apply false
}

group = "org.example"
version = "1.0-SNAPSHOT"

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
//        compileTestKotlin {
//            kotlinOptions {
//                jvmTarget = "1.8"
//            }
//        }
        withType<Test> {
            useJUnitPlatform()
        }
    }
}
