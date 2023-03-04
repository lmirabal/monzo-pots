plugins {
    id("lmirabal.library")
    `java-test-fixtures`
}

dependencies {
    api(projects.finance)

    testFixturesImplementation(libs.junit.api)
    testFixturesImplementation(libs.kotlin.test)
}