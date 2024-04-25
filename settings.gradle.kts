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
<<<<<<< HEAD
        maven ( "https://jitpack.io" )
=======
        maven (  "https://jitpack.io" )

>>>>>>> trimming
    }
}

rootProject.name = "mergeVideoJava"
include(":app")
 