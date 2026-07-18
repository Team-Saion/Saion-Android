plugins {
    id("com.saion.feature.impl")
}

android {
    namespace = "com.saion.feature.profileedit.impl"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(project(":core:design-system"))
    implementation(project(":feature:profile-edit:api"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
