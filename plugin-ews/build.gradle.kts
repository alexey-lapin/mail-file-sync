plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${Versions.mn}")

    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("io.micronaut:micronaut-inject:${Versions.mn}")
    implementation("com.microsoft.ews-java-api:ews-java-api:2.0")
}

