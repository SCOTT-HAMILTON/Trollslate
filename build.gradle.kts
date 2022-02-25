buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {}

allprojects {
    repositories {
        google()
        mavenCentral()
        @Suppress("JcenterRepositoryObsolete") // Required by dokka plugin
        jcenter()
    }
}
