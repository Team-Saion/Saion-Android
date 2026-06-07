package com.saion.convention

import com.saion.convention.internal.configureAndroidApplication
import com.saion.convention.internal.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class SaionAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")

        configureAndroidApplication()
        configureKotlinAndroid()
    }
}
