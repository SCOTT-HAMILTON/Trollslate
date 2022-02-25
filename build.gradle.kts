buildscript {
    repositories {
        google()
        mavenCentral()
//        maven("https://kotlin.bintray.com/kotlinx")
    }
    ext {
        this.c
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
//        classpath("com.karumi:shot:5.10.3")
    }
}

plugins {

}

allprojects {
    repositories {
        google()
        mavenCentral()
//        maven("https://kotlin.bintray.com/kotlinx/")
        @Suppress("JcenterRepositoryObsolete") // Required by dokka plugin
        jcenter()
    }
}