@Suppress("DSL_SCOPE_VIOLATION") // Because of Bug KTIJ-19370 in IDE.
plugins {
    id("lmirabal.library")
    alias(libs.plugins.kotlin.serialisation)
}

dependencies {
    implementation(projects.api)
    implementation(libs.http4k.core)
    implementation(libs.http4k.serialisation.kotlinx)
    implementation(libs.kotlinx.datetime)
}