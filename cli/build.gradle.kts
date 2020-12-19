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
    kapt("io.micronaut:micronaut-inject-java:${Versions.mn}")

    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation(project(":plugin-ews"))
    implementation(project(":plugin-imap"))
    implementation(project(":plugin-smtp"))
    implementation("com.github.ajalt.clikt:clikt:3.0.1")
    implementation("io.micronaut:micronaut-inject:${Versions.mn}")

    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}
