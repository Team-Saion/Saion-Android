package com.saion.convention.internal

import org.gradle.api.Project

internal fun Project.requirePairedFeatureApiProjectPath(): String {
    val segments = path.split(":")
    require(segments.size == 4 && segments[1] == "feature" && segments[3] == "impl") {
        "The com.saion.feature.impl plugin can only be applied to :feature:<name>:impl projects. Actual path: $path"
    }

    return ":feature:${segments[2]}:api"
}
