plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.saion.buildlogic"

gradlePlugin {
    plugins {
        register("saionAndroidApplication") {
            id = "com.saion.android.application"
            implementationClass = "com.saion.convention.SaionAndroidApplicationPlugin"
        }
        register("saionAndroidLibrary") {
            id = "com.saion.android.library"
            implementationClass = "com.saion.convention.SaionAndroidLibraryPlugin"
        }
        register("saionKotlinLibrary") {
            id = "com.saion.kotlin.library"
            implementationClass = "com.saion.convention.SaionKotlinLibraryPlugin"
        }
        register("saionAndroidCompose") {
            id = "com.saion.android.compose"
            implementationClass = "com.saion.convention.SaionAndroidComposePlugin"
        }
        register("saionAndroidHilt") {
            id = "com.saion.android.hilt"
            implementationClass = "com.saion.convention.SaionAndroidHiltPlugin"
        }
        register("saionAndroidTesting") {
            id = "com.saion.android.testing"
            implementationClass = "com.saion.convention.SaionAndroidTestingPlugin"
        }
        register("saionFeatureApi") {
            id = "com.saion.feature.api"
            implementationClass = "com.saion.convention.SaionFeatureApiPlugin"
        }
        register("saionFeatureImpl") {
            id = "com.saion.feature.impl"
            implementationClass = "com.saion.convention.SaionFeatureImplPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}
