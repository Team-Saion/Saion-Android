package com.saion.convention

import com.saion.convention.internal.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class SaionKotlinLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            configureKotlinJvm()
        }
}
