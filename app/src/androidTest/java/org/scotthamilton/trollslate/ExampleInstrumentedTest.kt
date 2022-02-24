package org.scotthamilton.trollslate

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme
import tools.fastlane.screengrab.FileWritingScreenshotCallback
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val localeTestRule = LocaleTestRule()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("org.scotthamilton.trollslate", appContext.packageName)
    }

    @Test
    fun mainActivityScreenshots() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())

        composeTestRule.setContent {
            TrollslateTheme {
                MainActivityContent(null)
            }
        }
        composeTestRule.onNodeWithTag("gyroFab").performClick()
        composeTestRule.takeScreenShot("start")
    }

    @Test
    fun trollActivityScreenshots() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        composeTestRule.setContent {
            TrollslateTheme {
                TrollActivityContent("HELLO WORLD "*5, 10f)
            }
        }
        composeTestRule.onNodeWithTag("trollActivityLazyRow").performScrollToIndex(12)
        composeTestRule.takeScreenShot("troll")
    }
}

// Thanks to https://dev.to/pchmielowski/automate-taking-screenshots-of-android-app-with-jetpack-compose-2950
private fun ComposeContentTestRule.takeScreenShot(screenName: String) {
    waitForIdle()
    onRoot()
        .captureToImage()
        .asAndroidBitmap()
        .saveScreengrab(screenName)
}

private fun Bitmap.saveScreengrab(file: String) {
    FileWritingScreenshotCallback(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        Screengrab.getLocale()
    ).screenshotCaptured(file, this)
}

private operator fun String.times(i: Int): String =
    List(i) { this }.joinToString("")
