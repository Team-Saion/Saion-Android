plugins {
    id("com.saion.feature.impl")
}

android {
    namespace = "com.saion.feature.main.impl"
}

dependencies {
    implementation(projects.feature.home.api)
    implementation(projects.feature.search.api)
    implementation(projects.feature.notification.api)
    implementation(projects.feature.mypage.api)
}
