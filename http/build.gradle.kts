plugins {
    id("lmirabal.project")
    application
}

dependencies {
    implementation(libs.http4k.jetty)

    testImplementation(libs.http4k.okhttp)
    testImplementation(libs.http4k.hamkrest)
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