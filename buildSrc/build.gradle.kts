plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.testlogger.gradle)
    implementation(libs.kotlin.gradle)
    // Required to access version catalog
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}