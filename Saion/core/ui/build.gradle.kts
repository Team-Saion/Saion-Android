plugins {
    id("com.saion.android.library")
    id("com.saion.android.compose")
    id("com.saion.android.testing")
}

android {
    namespace = "com.saion.core.ui"
}

ktlint {
    additionalEditorconfig.put(
        "ktlint_compose_compositionlocal-allowlist",
        "disabled",
    )
}

dependencies {
    implementation(project(":core:design-system"))
    implementation(projects.core.model)

    implementation(libs.androidx.compose.animation)
    implementation(libs.timber)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    testImplementation(libs.kotlinx.coroutines.test)
}
