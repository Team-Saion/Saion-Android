plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.notification.api"
}

dependencies {
    api(projects.feature.main.api)
}
