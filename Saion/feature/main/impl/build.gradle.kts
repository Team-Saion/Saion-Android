plugins {
    id("com.saion.feature.impl")
}

android {
    namespace = "com.saion.feature.main.impl"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.feature.circleCreate.api)
    implementation(projects.feature.home.api)
    implementation(projects.feature.schedule.api)
    implementation(projects.feature.mypage.api)

    implementation(libs.androidx.activity.compose)
}
