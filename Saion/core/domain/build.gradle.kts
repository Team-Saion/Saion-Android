plugins {
    id("com.saion.kotlin.library")
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
