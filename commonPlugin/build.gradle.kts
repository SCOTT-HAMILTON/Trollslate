plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("offline-dependencies")
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())

    implementation("com.android.tools.build:gradle:7.2.0-beta02")
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
