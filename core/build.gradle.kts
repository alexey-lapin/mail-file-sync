plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-library`
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${Versions.mn}")
    implementation(kotlin("stdlib"))
    implementation("io.micronaut:micronaut-inject:${Versions.mn}")
    api("io.github.microutils:kotlin-logging-jvm:2.0.2")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
}
