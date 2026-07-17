plugins {
    id("com.saion.android.application")
    id("com.saion.android.compose")
    id("com.saion.android.hilt")
    id("com.saion.android.testing")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.saion.app"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.saion.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.logging)
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.auth)
    implementation(project(":core:design-system"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.collections.immutable)

    implementation(projects.feature.auth.api)
    implementation(projects.feature.auth.impl)

    implementation(projects.feature.main.api)
    implementation(projects.feature.main.impl)

    implementation(projects.feature.home.api)
    implementation(projects.feature.home.impl)

    implementation(projects.feature.circleCreate.api)
    implementation(projects.feature.circleCreate.impl)

    implementation(projects.feature.schedule.api)
    implementation(projects.feature.schedule.impl)

    implementation(projects.feature.mypage.api)
    implementation(projects.feature.mypage.impl)

    testImplementation(libs.kotlinx.coroutines.test)
}
