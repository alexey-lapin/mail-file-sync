plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${Versions.mn}")

    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("io.micronaut:micronaut-inject:${Versions.mn}")
    implementation("com.sun.mail:jakarta.mail:${Versions.jakartaMail}")
    implementation("jakarta.mail:jakarta.mail-api:${Versions.jakartaMail}")
}
