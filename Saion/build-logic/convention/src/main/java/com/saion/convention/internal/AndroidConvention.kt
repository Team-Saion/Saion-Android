package com.saion.convention.internal

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureAndroidApplication() {
    extensions.configure<ApplicationExtension> {
        configureAndroidCommon()

        defaultConfig {
            minSdk = BuildLogicConstants.MIN_SDK
            targetSdk = BuildLogicConstants.TARGET_SDK
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }
    }
}

internal fun Project.configureAndroidLibrary() {
    extensions.configure<LibraryExtension> {
        configureAndroidCommon()

        defaultConfig {
            minSdk = BuildLogicConstants.MIN_SDK
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
}

private fun ApplicationExtension.configureAndroidCommon() {
    compileSdk = BuildLogicConstants.COMPILE_SDK

    compileOptions {
        sourceCompatibility = BuildLogicConstants.javaVersion
        targetCompatibility = BuildLogicConstants.javaVersion
    }
}

private fun LibraryExtension.configureAndroidCommon() {
    compileSdk = BuildLogicConstants.COMPILE_SDK

    compileOptions {
        sourceCompatibility = BuildLogicConstants.javaVersion
        targetCompatibility = BuildLogicConstants.javaVersion
    }
}
