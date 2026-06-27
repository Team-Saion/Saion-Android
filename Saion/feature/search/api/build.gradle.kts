plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.search.api"
}

dependencies {
    api(projects.feature.main.api)
}
