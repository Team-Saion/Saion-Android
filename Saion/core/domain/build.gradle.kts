plugins {
    id("com.saion.kotlin.library")
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
}
