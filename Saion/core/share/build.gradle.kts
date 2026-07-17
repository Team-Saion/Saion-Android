import com.android.build.api.variant.BuildConfigField
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.saion.android.library")
    id("com.saion.android.hilt")
}

android {
    namespace = "com.saion.core.share"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kakao.share)
    implementation(libs.kotlinx.coroutines.core)
}

androidComponents {
    onVariants { variant ->
        val buildConfigFields = variant.buildConfigFields ?: return@onVariants
        val properties = gradleLocalProperties(
            projectRootDir = rootDir,
            providers = providers,
        )
        val kakaoNativeAppKey = properties.getProperty("kakao.native.app.key").orEmpty()
        val invitationTemplateId = properties.getProperty("kakao.invitation.template.id")
            ?.ifBlank { "135148" }
            ?: "135148"

        buildConfigFields.put(
            "KAKAO_NATIVE_APP_KEY",
            BuildConfigField("String", "\"$kakaoNativeAppKey\"", null),
        )
        buildConfigFields.put(
            "KAKAO_INVITATION_TEMPLATE_ID",
            BuildConfigField("long", "${invitationTemplateId}L", null),
        )
    }
}
