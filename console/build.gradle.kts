plugins {
    id("lmirabal.project")
    application
}

dependencies {
    implementation(projects.application)
    implementation(projects.monzoAdapter)
}

application {
    mainClass.set("lmirabal.MainKt")
}
