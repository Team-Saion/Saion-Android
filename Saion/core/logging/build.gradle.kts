plugins {
    id("com.saion.android.library")
    id("com.saion.android.testing")
}

android {
    namespace = "com.saion.core.logging"
}

dependencies {
    implementation(libs.logger)
    implementation(libs.timber)
}
