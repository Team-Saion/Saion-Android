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
    // Temporary override:
    // foundation 1.11.4 fails SaionButton disabled-style redraw on enabled -> false transition.
    // Verified fixed by SaionButtonTest with foundation 1.12.0-alpha01.
    // Remove when same fix is available in stable Compose Foundation.
    implementation("androidx.compose.foundation:foundation:1.12.0-alpha01")
    implementation(libs.lottie.compose)
    testImplementation(libs.kotlinx.coroutines.test)
}
