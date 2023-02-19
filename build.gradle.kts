plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    application
}

group = "lmirabal"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.http4k.core)
    implementation(libs.http4k.serialisation.kotlinx)
    implementation(libs.kotlinx.datetime)
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.params)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("lmirabal.MainKt")
}