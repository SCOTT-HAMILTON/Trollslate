buildscript {
    repositories {
        google()
        mavenCentral()
        maven{
            url = uri("./app/offline-repository")
        }
        maven{
            url = uri("./offline-repository")
        }
    }
//    configurations.classpath {
//        resolutionStrategy {
//            repositories {
//                google()
//                mavenCentral()
//                maven{
//                    url = uri("./app/offline-repository")
//                }
//                maven{
//                    url = uri("./offline-repository")
//                }
//            }
//        }
//    }
    dependencies {
        repositories {
            google()
            mavenCentral()
            maven{
                url = uri("./app/offline-repository")
            }
            maven{
                url = uri("./offline-repository")
            }
        }
        classpath("com.android.tools.build:gradle:7.2.0-beta02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    id("offline-dependencies")
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven{
            url = uri("./app/offline-repository")
        }
        maven{
            url = uri("./offline-repository")
        }
    }
}
