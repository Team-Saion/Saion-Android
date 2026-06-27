plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.home.api"
}

dependencies {
    api(projects.feature.main.api)
}
