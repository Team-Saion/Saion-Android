import com.android.build.api.variant.BuildConfigField
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.saion.android.library")
    id("com.saion.android.hilt")
}

android {
    namespace = "com.saion.core.auth"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kakao.user)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

androidComponents {
    onVariants { variant ->
        val buildConfigFields = variant.buildConfigFields ?: return@onVariants
        val kakaoNativeAppKey = gradleLocalProperties(
            projectRootDir = rootDir,
            providers = providers,
        ).getProperty("kakao.native.app.key").orEmpty()

        buildConfigFields.put(
            "KAKAO_NATIVE_APP_KEY",
            BuildConfigField("String", "\"$kakaoNativeAppKey\"", null),
        )

        variant.manifestPlaceholders.put(
            "kakaoOauthScheme",
            "kakao$kakaoNativeAppKey",
        )
    }
}
