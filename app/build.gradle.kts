plugins {
    id("offline-dependencies")
    id("com.android.application")
    id("common-config")
}

android {
    // signingConfigs {
    //     create("config") {
    //         storeFile = file("/home/scott/Android/upload-keystore.jks")
    //         storePassword = "test1234"
    //         keyAlias = "upload"
    //         keyPassword = "test1234"
    //     }
    // }
    kotlinOptions { jvmTarget = "1.8" }
    defaultConfig {
        applicationId = "org.scotthamilton.trollslate"
        // signingConfig = signingConfigs.getByName("config")
        //        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = Libs.Compose.version }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFile(file("proguard-rules.pro"))
        }
    }
    namespace = "org.scotthamilton.trollslate"
}

dependencies {
    implementations(Libs.AndroidX.main + listOf(Libs.Compose.navigation))
    implementations(Libs.Compose.main)
    add("testImplementation", Libs.junit)
    androidTestImplementations(
        Libs.AndroidX.androidTest + Libs.Compose.androidTest + listOf(Libs.screengrab)
    )
    debugImplementations(Libs.Compose.debugTest)
}
