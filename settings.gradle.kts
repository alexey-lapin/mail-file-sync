pluginManagement {
    plugins {
        kotlin("jvm") version settings.extra["version.kt"] as String
        kotlin("kapt") version settings.extra["version.kt"] as String
        id("io.micronaut.library") version "1.2.0"
        id("com.diffplug.spotless") version "5.11.1"
        id("com.github.ben-manes.versions") version "0.38.0"
        id("com.github.johnrengelman.shadow") version "6.1.0"
    }
}

rootProject.name = "ewss"
include("core")
include("plugin-ews")
include("plugin-imap")
include("plugin-smtp")
include("cli")
