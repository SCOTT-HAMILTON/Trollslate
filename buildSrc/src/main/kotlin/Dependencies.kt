import org.gradle.api.artifacts.dsl.DependencyHandler

const val kotlinVersion = "1.5.31"
const val gradleVersion = "7.4"

object Libs {
    const val desugar = "com.android.tools:desugar_jdk_libs:1.1.5"
    const val junit = "junit:junit:4.13.2"
    const val mockk = "io.mockk:mockk:1.12.2"
    const val screengrab = "tools.fastlane:screengrab:2.1.1"
    object AndroidX {
        const val core = "androidx.core:core-ktx:1.6.0"
        const val appcompat = "androidx.appcompat:appcompat:1.3.1"

        const val extJunit = "androidx.test.ext:junit:1.1.3"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
        const val runner = "androidx.test:runner:1.4.0"
        const val rules = "androidx.test:rules:1.4.0"
        const val uiautomator = "androidx.test.uiautomator:uiautomator:2.2.0"

        val main = listOf(core, appcompat)
        val androidTest = listOf(extJunit, espresso, runner, rules, uiautomator)
    }

    object Compose {
        const val version = "1.0.5"

        const val animation = "androidx.compose.animation:animation:$version"
        const val foundation = "androidx.compose.foundation:foundation:$version"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout:$version"
        const val materialIcons =
            "androidx.compose.material:material-icons-extended:$version"
        const val material3 = "androidx.compose.material3:material3:1.0.0-alpha06"
        const val material = "androidx.compose.material:material:$version"
        const val runtime = "androidx.compose.runtime:runtime:$version"
        const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
        const val ui = "androidx.compose.ui:ui:$version"
        const val uiUtil = "androidx.compose.ui:ui-util:$version"
        const val activity = "androidx.activity:activity-compose:1.4.0"

        const val uiTest = "androidx.compose.ui:ui-test:$version"
        const val uiTestJUnit = "androidx.compose.ui:ui-test-junit4:$version"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:$version"

        val main = listOf(
            animation,
            foundation,
            foundationLayout,
            materialIcons,
            material3,
            material,
            runtime,
            uiTooling,
            ui,
            uiUtil,
            activity
        )

        val androidTest = listOf(
            uiTest,
            uiTestJUnit
        )

        val debugTest = listOf(uiTooling, uiTestManifest)
    }
}

fun DependencyHandler.implementations(dependencies: List<String>) {
    dependencies.forEach {
        add("implementation", it)
    }
}

fun DependencyHandler.androidTestImplementations(dependencies: List<String>) {
    dependencies.forEach {
        add("androidTestImplementation", it)
    }
}

fun DependencyHandler.debugImplementations(dependencies: List<String>) {
    dependencies.forEach {
        add("debugImplementation", it)
    }
}

