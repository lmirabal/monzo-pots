@Suppress("DSL_SCOPE_VIOLATION") // Because of Bug KTIJ-19370 in IDE.
plugins {
    id("lmirabal.project")
    application
    alias(libs.plugins.kotlin.serialisation)
}

dependencies {
    implementation(projects.application)
    implementation(projects.monzoAdapter)
    implementation(libs.http4k.jetty)
    implementation(libs.http4k.serialisation.kotlinx)

    testImplementation(libs.http4k.okhttp)
    testImplementation(libs.http4k.hamkrest)
    testImplementation(testFixtures(projects.api))
}

application {
    mainClass.set("lmirabal.MainKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}