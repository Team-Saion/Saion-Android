plugins {
    id("com.saion.android.library")
    id("com.saion.android.compose")
    id("com.saion.android.testing")
}

android {
    namespace = "com.saion.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.collections.immutable)
}
