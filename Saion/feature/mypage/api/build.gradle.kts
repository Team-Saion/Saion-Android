plugins {
    id("com.saion.feature.api")
}

android {
    namespace = "com.saion.feature.mypage.api"
}

dependencies {
    api(projects.feature.main.api)
}
