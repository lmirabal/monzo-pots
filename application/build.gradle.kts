@Suppress("DSL_SCOPE_VIOLATION") // Because of Bug KTIJ-19370 in IDE.
plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.testlogger)
    alias(libs.plugins.kotlin.serialisation)
}

group = "lmirabal"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.http4k.core)
    implementation(libs.http4k.serialisation.kotlinx)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.params)
}

tasks.test {
    useJUnitPlatform()
    systemProperties["junit.jupiter.execution.parallel.enabled"] = "true"
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "same_thread"
    systemProperties["junit.jupiter.execution.parallel.mode.classes.default"] = "concurrent"
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
}

application {
    mainClass.set("lmirabal.MainKt")
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD_PARALLEL
}