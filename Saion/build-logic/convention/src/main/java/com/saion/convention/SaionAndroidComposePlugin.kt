package com.saion.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.saion.convention.internal.library
import com.saion.convention.internal.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class SaionAndroidComposePlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures {
                        compose = true
                    }
                }
            }

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures {
                        compose = true
                    }
                }
            }

            pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
                dependencies {
                    add("ktlintRuleset", libs.library("compose-rules-ktlint"))
                }
            }

            dependencies {
                add("implementation", platform(libs.library("androidx-compose-bom").get()))
                add("implementation", libs.library("androidx-compose-ui"))
                add("implementation", libs.library("androidx-compose-ui-graphics"))
                add("implementation", libs.library("androidx-compose-ui-tooling-preview"))
                add("implementation", libs.library("androidx-compose-material3"))
                add("androidTestImplementation", platform(libs.library("androidx-compose-bom").get()))
                add("androidTestImplementation", libs.library("androidx-compose-ui-test-junit4"))
                add("debugImplementation", libs.library("androidx-compose-ui-test-manifest"))
                add("debugImplementation", libs.library("androidx-compose-ui-tooling"))
            }
        }
}
