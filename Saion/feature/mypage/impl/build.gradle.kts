plugins {
    id("com.saion.feature.impl")
}

android {
    namespace = "com.saion.feature.mypage.impl"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.notification)
    implementation(project(":feature:profile-edit:api"))
    implementation(project(":feature:profile-edit:impl"))
    implementation(projects.feature.terms.api)
    implementation(projects.feature.terms.impl)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
