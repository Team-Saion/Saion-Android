plugins {
    id("com.saion.feature.impl")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.saion.feature.auth.impl"
}

dependencies {
    implementation(projects.core.auth)
    implementation(project(":core:design-system"))
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.notification)
    implementation(projects.feature.main.api)
    implementation(project(":feature:profile-edit:impl"))
    implementation(projects.feature.terms.api)
    implementation(projects.feature.terms.impl)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
//    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}
