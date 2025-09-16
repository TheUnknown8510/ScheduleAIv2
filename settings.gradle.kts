pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "2.2.0"
        id("com.android.application") version "8.13.0"
        // Remove all 'alias(libs.plugins....)' lines unless you are using version catalogs
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Schedule"
include(":app")