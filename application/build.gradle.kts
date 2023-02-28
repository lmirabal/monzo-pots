plugins {
    id("lmirabal.library")
}


dependencies {
    api(projects.api)
    implementation(projects.finance)

    testImplementation(testFixtures(projects.api))
}
