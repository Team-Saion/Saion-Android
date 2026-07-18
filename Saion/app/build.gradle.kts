import com.android.build.api.variant.BuildConfigField
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.File
import org.gradle.api.GradleException

plugins {
    id("com.saion.android.application")
    id("com.saion.android.compose")
    id("com.saion.android.hilt")
    id("com.saion.android.testing")
    alias(libs.plugins.google.services)
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

    val properties = gradleLocalProperties(
        projectRootDir = rootDir,
        providers = providers,
    )
    val releaseStoreFile = properties.getProperty("release.signing.store.file")
    val releaseStorePassword = properties.getProperty("release.signing.store.password")
    val releaseKeyAlias = properties.getProperty("release.signing.key.alias")
    val releaseKeyPassword = properties.getProperty("release.signing.key.password")
    val releaseSigningConfigured = listOf(
        releaseStoreFile,
        releaseStorePassword,
        releaseKeyAlias,
        releaseKeyPassword,
    ).all { !it.isNullOrBlank() }

    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = File(requireNotNull(releaseStoreFile))
                storePassword = requireNotNull(releaseStorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    buildTypes {
        if (releaseSigningConfigured) {
            getByName("release") {
                signingConfig = signingConfigs.getByName("release")
            }
            getByName("internal") {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}

val validateReleaseSigningConfig = tasks.register("validateReleaseSigningConfig") {
    doLast {
        val properties = gradleLocalProperties(
            projectRootDir = rootDir,
            providers = providers,
        )
        val requiredKeys = listOf(
            "release.signing.store.file",
            "release.signing.store.password",
            "release.signing.key.alias",
            "release.signing.key.password",
        )
        val missingKeys = requiredKeys.filter { properties.getProperty(it).isNullOrBlank() }

        if (missingKeys.isNotEmpty()) {
            throw GradleException(
                "Missing release signing properties in local.properties: " +
                    missingKeys.joinToString(),
            )
        }
    }
}

tasks.configureEach {
    if (name == "preReleaseBuild" || name == "preInternalBuild") {
        dependsOn(validateReleaseSigningConfig)
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
    implementation(projects.core.notification)
    implementation(projects.core.share)
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
    implementation(projects.feature.invitation.api)
    implementation(projects.feature.invitation.impl)

    implementation(projects.feature.circleCreate.api)
    implementation(projects.feature.circleCreate.impl)

    implementation(projects.feature.schedule.api)
    implementation(projects.feature.schedule.impl)

    implementation(projects.feature.mypage.api)
    implementation(projects.feature.mypage.impl)

    testImplementation(libs.kotlinx.coroutines.test)
}

androidComponents {
    onVariants { variant ->
        val properties = gradleLocalProperties(
            projectRootDir = rootDir,
            providers = providers,
        )
        val kakaoNativeAppKey = properties.getProperty("kakao.native.app.key").orEmpty()

        variant.manifestPlaceholders.put(
            "kakaoShareScheme",
            "kakao$kakaoNativeAppKey",
        )

        variant.buildConfigFields?.put(
            "KAKAO_SHARE_SCHEME",
            BuildConfigField("String", "\"kakao$kakaoNativeAppKey\"", null),
        )
    }
}
