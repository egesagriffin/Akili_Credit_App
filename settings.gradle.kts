pluginManagement {
    repositories {
        // If you are in a region with restricted access to Google, you can try Aliyun mirrors:
        // maven { url = uri("https://maven.aliyun.com/repository/google") }
        // maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // maven { url = uri("https://maven.aliyun.com/repository/google") }
        // maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
    }
}

rootProject.name = "Akili Credit"
include(":app")
