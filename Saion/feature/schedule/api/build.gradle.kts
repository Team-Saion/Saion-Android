plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.schedule.api"
}

dependencies {
    api(projects.feature.main.api)
}
