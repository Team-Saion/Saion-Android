plugins {
    id("com.saion.android.library")
    id("com.saion.android.hilt")
}

android {
    namespace = "com.saion.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.datastore)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
