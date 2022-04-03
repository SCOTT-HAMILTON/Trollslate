buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        //        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        //        classpath("com.android.tools.build:gradle:7.2.0-beta02")
    }
}

plugins {
    id("common-config") apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
