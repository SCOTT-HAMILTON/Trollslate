plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "org.scotthamilton"

version = "1.0"

repositories {
    google()
    mavenCentral()
    maven { url = uri("./../app/offline-repository") }
    maven { url = uri("./../offline-repository") }
}

dependencies {
    compileOnly(gradleApi())

    // implementation("com.android.tools.build:gradle:7.2.0-beta02")
    implementation(kotlin("gradle-plugin", "1.5.31"))
    implementation(kotlin("android-extensions"))
    implementation("org.apache.maven:maven-model-builder:3.8.4")
}

gradlePlugin {
    plugins {
        register("offline-dependencies") {
            id = "offline-dependencies"
            implementationClass = "offlinedependencies.OfflineDependenciesPlugin"
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("./../app/offline-repository") }
        maven { url = uri("./../offline-repository") }
    }
}
