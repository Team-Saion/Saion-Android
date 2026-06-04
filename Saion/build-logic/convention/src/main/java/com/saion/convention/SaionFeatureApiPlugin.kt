package com.saion.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class SaionFeatureApiPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("com.saion.android.library")
        }
}
