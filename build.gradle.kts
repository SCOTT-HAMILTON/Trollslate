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
    id("com.android.application") version "7.2.0-beta02" apply false
    //    id("com.android.library") apply false
    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
    //    id("java-gradle-plugin") apply false
    //    id("com.gradle.enterprise") version("3.8.1") apply false
    id("common-config") apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
