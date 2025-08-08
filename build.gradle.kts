// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    kotlin("kapt") apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // If you ever need to add classpath dependencies (for legacy builds), add them here
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
