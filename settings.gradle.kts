@file:Suppress("UnstableApiUsage")

import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

plugins {
    val kotlinVersion: String by settings

    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    kotlin("jvm") version kotlinVersion apply false //fixme due to bug, kotlin has to be declared here again
    id("org.jetbrains.intellij.platform.settings") version "2.13.1"
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    repositories {
        mavenCentral()
        google()

        intellijPlatform {
            defaultRepositories()
        }
    }
}

rootProject.name = "localizations-exporter"