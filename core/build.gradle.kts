plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-library`
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${versions.mn}")
    implementation(kotlin("stdlib", versions.kt))
    implementation("io.micronaut:micronaut-inject:${versions.mn}")
    api("io.github.microutils:kotlin-logging-jvm:2.0.4")

    testImplementation(kotlin("test-junit5", versions.kt))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions.junit}")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions.junit}")
}
