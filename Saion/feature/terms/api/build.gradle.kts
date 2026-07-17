plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.terms.api"
}

dependencies {
    implementation(projects.feature.main.api)
}
