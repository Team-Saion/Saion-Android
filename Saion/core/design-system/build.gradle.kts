plugins {
    id("com.saion.android.library")
    id("com.saion.android.compose")
    id("com.saion.android.testing")
}

android {
    namespace = "com.saion.core.designsystem"
}

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
}
