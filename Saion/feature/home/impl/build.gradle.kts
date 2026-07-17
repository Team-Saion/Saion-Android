plugins {
    id("com.saion.feature.impl")
}

android {
    namespace = "com.saion.feature.home.impl"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.share)
    implementation(projects.feature.circleCreate.api)
    implementation(projects.feature.schedule.api)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
