plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib", versions.kt))
    implementation(project(":core"))
    implementation("jakarta.mail:jakarta.mail-api:${versions.jakartaMail}")
    implementation("com.sun.mail:jakarta.mail:${versions.jakartaMail}")
}
