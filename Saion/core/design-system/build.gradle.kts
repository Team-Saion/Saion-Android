plugins {
    id("com.saion.android.library")
    id("com.saion.android.compose")
    id("com.saion.android.testing")
}

android {
    namespace = "com.saion.core.designsystem"
}

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }
}

dependencies {
    implementation(libs.lottie.compose)
    testImplementation(libs.kotlinx.coroutines.test)
}
