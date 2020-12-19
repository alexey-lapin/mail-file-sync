pluginManagement {
    plugins {
        kotlin("jvm") version "1.4.20"
        kotlin("kapt") version "1.4.20"
        id("io.micronaut.library") version "1.2.0"
        id("com.github.johnrengelman.shadow") version "6.1.0"
    }
}

rootProject.name = "ewss"
include("core")
include("cli")
include("plugin-ews")
include("plugin-imap")
include("plugin-smtp")
