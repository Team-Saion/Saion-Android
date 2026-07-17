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
        maven(url = "https://devrepo.kakao.com/nexus/content/groups/public/")
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
include(":core:auth")
include(":core:share")
include(":core:design-system")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:main:api")
include(":feature:main:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:invitation:api")
include(":feature:invitation:impl")
include(":feature:circle-create:api")
include(":feature:circle-create:impl")
include(":feature:schedule:api")
include(":feature:schedule:impl")
include(":feature:mypage:api")
include(":feature:mypage:impl")
