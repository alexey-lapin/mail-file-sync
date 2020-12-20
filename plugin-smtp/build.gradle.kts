plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${versions.mn}")

    implementation(kotlin("stdlib", versions.kt))
    implementation(project(":core"))
    implementation("io.micronaut:micronaut-inject:${versions.mn}")
    implementation("com.sun.mail:jakarta.mail:${versions.jakartaMail}")
    implementation("jakarta.mail:jakarta.mail-api:${versions.jakartaMail}")
}
