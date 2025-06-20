pluginManagement {
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

//        maven {
//            url = rootProject.projectDir.toURI().resolve("libs")
//        }
//        maven {
//            url = uri("https://jitpack.io")
//        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://sdk.paymob.com/maven")
        }
        maven {
            url = rootProject.projectDir.toURI().resolve("libs")
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "M-Commerce"
include(":app")
 