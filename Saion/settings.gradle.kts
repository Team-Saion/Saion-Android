pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/Team-Saion/Saion-Android-DS")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                password = providers.gradleProperty("gpr.key").orNull
            }
        }
    }
}

rootProject.name = "Saion"
include(":app")
include(":core:data")
include(":core:domain")
include(":core:datastore")
include(":core:logging")
include(":core:model")
include(":core:navigation")
include(":core:network")
include(":core:ui")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:main:api")
include(":feature:main:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:notification:api")
include(":feature:notification:impl")
include(":feature:mypage:api")
include(":feature:mypage:impl")
