import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.adarshr.test-logger")
}

group = "lmirabal"

repositories {
    mavenCentral()
}


val libs = the<LibrariesForLibs>()
dependencies {
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

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD_PARALLEL
}