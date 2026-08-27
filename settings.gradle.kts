pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CodeLearn"

include(":app")
include(":core-model")
include(":core-curriculum")
include(":core-data")
include(":core-editor")
include(":core-execution")
