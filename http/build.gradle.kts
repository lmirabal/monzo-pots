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