@Suppress("DSL_SCOPE_VIOLATION") // Because of Bug KTIJ-19370 in IDE.
plugins {
    id("lmirabal.project")
    application
    alias(libs.plugins.kotlin.serialisation)
}


dependencies {
    implementation(libs.http4k.core)
    implementation(libs.http4k.serialisation.kotlinx)
    implementation(libs.kotlinx.datetime)
}

application {
    mainClass.set("lmirabal.MainKt")
}