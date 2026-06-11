import com.android.build.api.variant.BuildConfigField
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.saion.android.library")
    id("com.saion.android.hilt")
}

android {
    namespace = "com.saion.core.network"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.timber)
}

androidComponents {
    onVariants { variant ->
        val buildType = variant.buildType ?: return@onVariants
        val buildConfigFields = variant.buildConfigFields ?: return@onVariants
        val baseUrlKey = "$buildType.base.url"

        buildConfigFields.put(
            "BASE_URL",
            gradleLocalProperties(projectRootDir = rootDir, providers = providers)
                .getProperty(baseUrlKey)
                .let { value ->
                    BuildConfigField("String", "\"$value\"", null)
                },
        )
    }
}
