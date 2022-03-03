plugins {
    id("java-gradle-plugin")
    `kotlin-dsl`
//    id("org.jetbrains.kotlin.android") version "1.5.31"
    id("offline-dependencies")
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())

    compileOnly("com.android.tools.build:gradle:7.2.0-beta02")
    compileOnly("com.android.tools.build:gradle-api:7.2.0-beta02")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    implementation(kotlin("gradle-plugin", "1.5.31"))
    implementation(kotlin("android-extensions"))
}

gradlePlugin {
    plugins {
        register("common-config") {
            id = "common-config"
            implementationClass = "CommonConfigurationPlugin"
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
