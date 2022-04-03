package org.scotthamilton.trollslate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Build.VERSION
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.scotthamilton.trollslate.ui.MainActivityContent
import org.scotthamilton.trollslate.ui.TrollContent
import org.scotthamilton.trollslate.ui.defaultPhoneAngleSelectorData
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme
import org.scotthamilton.trollslate.ui.theme.trollslateColorScheme
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
    @get:Rule val composeTestRule = createComposeRule()

    @Rule @JvmField val localeTestRule = LocaleTestRule()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("org.scotthamilton.trollslate", appContext.packageName)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mainActivityScreenshots() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        composeTestRule.setContent {
            TrollslateTheme {
                MainActivityContent(
                    phoneAngleSelectorData = defaultPhoneAngleSelectorData(phone3DLetter = 'T')
                )
            }
        }
        composeTestRule.onNodeWithTag("phoneAngleScroller").performGesture {
            swipeDown(startY = 0f, endY = 10000f)
        }
        composeTestRule.onNodeWithTag("trollTextField").performTextInput("HELLO WORLD" * 5)
        composeTestRule.takeScreenShot("start")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun trollPageScreenshots() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        composeTestRule.setContent {
            TrollslateTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TrollContent("HELLO WORLD" * 5, 10f, colorScheme = trollslateColorScheme())
                }
            }
        }
        composeTestRule.takeScreenShot("troll")
        composeTestRule.onNodeWithTag("trollActivityLazyRow").performScrollToIndex(12)
    }
}

// Thanks to
// https://dev.to/pchmielowski/automate-taking-screenshots-of-android-app-with-jetpack-compose-2950
private fun ComposeContentTestRule.takeScreenShot(screenName: String) {
    waitForIdle()
    onRoot().captureToImage().asAndroidBitmap().saveScreengrab(screenName)
}

private fun Bitmap.saveScreengrab(name: String) {
    screenshotCaptured(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext,
        Screengrab.getLocale(),
        name,
        this
    )
}

fun getScreenshotFile(screenshotDirectory: File?, screenshotName: String): File {
    val screenshotFileName = screenshotName + System.currentTimeMillis() + ".png"
    return File(screenshotDirectory, screenshotFileName)
}

@Throws(IOException::class)
private fun getFilesDirectory(context: Context, locale: String): File {
    val base: File?
    base =
        if (VERSION.SDK_INT > 29) {
            context.getDir("screengrab", 0)
        } else if (VERSION.SDK_INT < 24) {
            context.getDir("screengrab", 1)
        } else {
            context.getExternalFilesDir("screengrab")
        }
    return if (base == null) {
        throw IOException("Unable to get a world-readable directory")
    } else {
        val directory = initializeDirectory(File(File(base, locale), "/images/screenshots"))
        if (directory == null) {
            throw IOException("Unable to get a screenshot storage directory")
        } else {
            dlog("Screengrab", "Using screenshot storage directory: " + directory.absolutePath)
            directory
        }
    }
}

private fun initializeDirectory(dir: File): File? {
    try {
        createPathTo(dir)
        if (dir.isDirectory && dir.canWrite()) {
            return dir
        }
    } catch (var2: IOException) {
        dlog("Screengrab", "Failed to initialize directory: " + dir.absolutePath + var2)
    }
    return null
}

@Throws(IOException::class)
private fun createPathTo(dir: File) {
    val parent = dir.parentFile
    if (parent != null && !parent.exists()) {
        createPathTo(parent)
    }
    if (!dir.exists() && !dir.mkdirs()) {
        throw IOException("Unable to create output dir: " + dir.absolutePath)
    }
}

private fun dlog(tag: String, msg: String) {
    println("$tag: $msg")
}

fun screenshotCaptured(
    context: Context,
    locale: String,
    screenshotName: String,
    screenshot: Bitmap
) {
    try {
        val screenshotDirectory = getFilesDirectory(context, locale)
        val screenshotFile: File = getScreenshotFile(screenshotDirectory, screenshotName)
        dlog("Screengrab", "screenshotFile=`$screenshotFile`")
        var fos: BufferedOutputStream? = null
        try {
            fos = BufferedOutputStream(FileOutputStream(screenshotFile))
            screenshot.compress(CompressFormat.PNG, 100, fos)
        } finally {
            screenshot.recycle()
            fos?.close()
        }
        dlog("Screengrab", "Captured screenshot \"" + screenshotFile.name + "\"")
    } catch (var10: Exception) {
        throw RuntimeException("Unable to capture screenshot.", var10)
    }
}

private operator fun String.times(i: Int): String = List(i) { this }.joinToString("")
