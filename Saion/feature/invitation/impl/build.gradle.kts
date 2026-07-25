plugins {
    id("com.saion.feature.impl")
}

android {
    namespace = "com.saion.feature.invitation.impl"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.feature.main.api)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
