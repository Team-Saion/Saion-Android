plugins {
    id("com.saion.feature.impl")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.saion.feature.auth.impl"
}

dependencies {
    implementation(projects.feature.main.api)
}
