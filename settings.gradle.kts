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
        google() // Add google() here
        mavenCentral()
        //moi them vao
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "tdm_social_media"
include(":app")