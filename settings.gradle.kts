pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    // Lets Gradle provision the Java 25 toolchain Minecraft 26.3 requires.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "do-a-barrel-roll-neoforge"
