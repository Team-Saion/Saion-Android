plugins {
    id("com.saion.android.library")
    id("com.saion.android.hilt")
}

android {
    namespace = "com.saion.core.local"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
}
