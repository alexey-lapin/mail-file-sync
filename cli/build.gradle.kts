import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
    kotlin("kapt")
}

application {
    mainClassName = "com.github.al.mfs.cli.MailFileSyncKt"
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${versions.mn}")

    implementation(kotlin("stdlib", versions.kt))
    implementation(project(":core"))
    implementation(project(":plugin-ews"))
    implementation(project(":plugin-imap"))
    implementation(project(":plugin-smtp"))
    implementation("com.github.ajalt.clikt:clikt:3.1.0")
    implementation("io.micronaut:micronaut-inject:${versions.mn}")

    runtimeOnly("ch.qos.logback:logback-classic:${versions.logback}")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}
