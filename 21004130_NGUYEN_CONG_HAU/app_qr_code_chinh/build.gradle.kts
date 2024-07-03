buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath ("com.google.gms:google-services:4.4.2")
        classpath ("com.android.tools.build:gradle:8.0.2") // Use the latest version available
        classpath ("com.google.gms:google-services:4.3.15") // Use the latest version available
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

