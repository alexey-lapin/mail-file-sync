plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${versions.mn}")

    implementation(kotlin("stdlib", versions.kt))
    implementation(project(":core"))
    implementation("io.micronaut:micronaut-inject:${versions.mn}")
    implementation("com.microsoft.ews-java-api:ews-java-api:2.0")
    implementation("javax.xml.ws:jaxws-api:2.3.1")
}
