package com.saion.convention.internal

import org.gradle.api.JavaVersion

internal object BuildLogicConstants {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 28
    const val TARGET_SDK = 36
    val javaVersion: JavaVersion = JavaVersion.VERSION_21
}
