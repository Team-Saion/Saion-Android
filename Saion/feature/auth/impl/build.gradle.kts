plugins {
    id("com.saion.feature.impl")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.saion.feature.auth.impl"
}

dependencies {
    implementation(projects.core.auth)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.feature.main.api)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}
