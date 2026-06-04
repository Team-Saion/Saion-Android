package com.saion.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class SaionFeatureImplPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("com.saion.android.library")
            pluginManager.apply("com.saion.android.compose")
            pluginManager.apply("com.saion.android.testing")
        }
}
