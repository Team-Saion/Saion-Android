plugins {
    id("com.saion.android.application")
    id("com.saion.android.compose")
    id("com.saion.android.hilt")
    id("com.saion.android.testing")
}

android {
    namespace = "com.saion.app"

    defaultConfig {
        applicationId = "com.saion.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(libs.saion.design.system)
}
