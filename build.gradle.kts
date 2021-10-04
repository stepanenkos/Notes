// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra("1.5.31")
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.0.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath ("com.google.gms:google-services:4.3.10")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven (url ="https://plugins.gradle.org/m2/")
    }
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}