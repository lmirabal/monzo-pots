plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    application
    alias(libs.plugins.testlogger)
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
    systemProperties["junit.jupiter.execution.parallel.enabled"] = "true"
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "same_thread"
    systemProperties["junit.jupiter.execution.parallel.mode.classes.default"] = "concurrent"
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("lmirabal.MainKt")
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD_PARALLEL
}