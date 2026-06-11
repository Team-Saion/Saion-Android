package com.saion.convention

import com.saion.convention.internal.configureAndroidLibrary
import com.saion.convention.internal.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class SaionAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("com.android.library")

            configureAndroidLibrary()
            configureKotlinAndroid()
        }
}
