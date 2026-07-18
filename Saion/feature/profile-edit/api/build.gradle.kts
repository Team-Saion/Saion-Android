plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.profileedit.api"
}

dependencies {
    api(projects.feature.main.api)
}
