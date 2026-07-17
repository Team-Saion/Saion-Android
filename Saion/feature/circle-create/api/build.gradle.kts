plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.circlecreate.api"
}

dependencies {
    api(projects.feature.main.api)
}
