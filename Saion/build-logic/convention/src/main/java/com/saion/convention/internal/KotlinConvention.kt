package com.saion.convention.internal

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.configureKotlinAndroid() {
    extensions.findByType(KotlinAndroidProjectExtension::class.java)?.apply {
        jvmToolchain(BuildLogicConstants.javaVersion.majorVersion.toInt())
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(BuildLogicConstants.javaVersion.majorVersion.toInt())
    }
}
