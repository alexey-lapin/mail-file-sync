plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("jakarta.mail:jakarta.mail-api:${Versions.jakartaMail}")
    implementation("com.sun.mail:jakarta.mail:${Versions.jakartaMail}")
}
