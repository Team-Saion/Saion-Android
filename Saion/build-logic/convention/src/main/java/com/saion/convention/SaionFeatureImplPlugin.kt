package com.saion.convention

import com.saion.convention.internal.requirePairedFeatureApiProjectPath
import com.saion.convention.internal.library
import com.saion.convention.internal.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SaionFeatureImplPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("com.saion.android.library")
            pluginManager.apply("com.saion.android.compose")
            pluginManager.apply("com.saion.android.testing")
            pluginManager.apply("com.saion.android.hilt")

            dependencies {
                add("implementation", project(":core:navigation"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:design-system"))
                add("implementation", libs.library("androidx-navigation3-runtime"))
                add("implementation", project(requirePairedFeatureApiProjectPath()))
            }
        }
}
