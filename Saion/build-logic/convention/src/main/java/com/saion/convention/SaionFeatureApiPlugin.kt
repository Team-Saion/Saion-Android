package com.saion.convention

import com.saion.convention.internal.library
import com.saion.convention.internal.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SaionFeatureApiPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.saion.android.library")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        dependencies {
            add("implementation", project(":core:navigation"))
            add("implementation", libs.library("androidx-navigation3-runtime"))
            add("implementation", libs.library("kotlinx-serialization-core"))
        }
    }
}
