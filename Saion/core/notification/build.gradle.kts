plugins {
    id("com.saion.android.library")
    id("com.saion.android.hilt")
}

android {
    namespace = "com.saion.core.notification"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)

    implementation(libs.androidx.core.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
