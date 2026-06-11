package com.saion.convention

import com.saion.convention.internal.library
import com.saion.convention.internal.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SaionAndroidTestingPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            dependencies {
                add("testImplementation", libs.library("junit"))
                add("androidTestImplementation", libs.library("androidx-junit"))
                add("androidTestImplementation", libs.library("androidx-espresso-core"))
            }
        }
}
