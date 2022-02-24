package org.scotthamilton.trollslate

import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import org.junit.Assert.assertEquals
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


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
        composeTestRule.waitForIdle()
        Screengrab.screenshot("start")
    }

    @Test
    fun trollActivityScreenshots() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        composeTestRule.setContent {
            TrollslateTheme {
                TrollActivityContent("HELLO WORLD "*5, 10f)
            }
        }
        composeTestRule.onNodeWithTag("trollActivityLazyRow").performScrollToIndex(5)
        composeTestRule.waitForIdle()
        Screengrab.screenshot("troll")
    }
}

private operator fun String.times(i: Int): String =
    List(i) { this }.joinToString("")
