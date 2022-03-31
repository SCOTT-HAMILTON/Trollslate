package org.scotthamilton.trollslate.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.PI
import kotlin.math.sin
import org.scotthamilton.trollslate.R
import org.scotthamilton.trollslate.ui.theme.OutlinedFloatingActionButton
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme
import org.scotthamilton.trollslate.ui.theme.md_theme_light_secondary

@RequiresApi(Build.VERSION_CODES.N)
@Composable
private fun TrollShareFab(
    activity: Activity?,
    modifier: Modifier = Modifier,
    text: String,
    angle: Float,
    colorScheme: ColorScheme
) {
    val letterSize = angleToLetterSize(angle)
    val strokeWidth = letterSizeToStokeWidth(letterSize)
    OutlinedFloatingActionButton(
        color = md_theme_light_secondary,
        onClick = {
            activity?.let { activity ->
                // Thanks to
                // https://github.com/IanDarwin/Android-Cookbook-Examples/blob/master/PdfShare/src/main/java/com/example/pdfshare/MainActivity.java
                try {
                    val pdfDirPath = File(activity.filesDir, "pdfs")
                    pdfDirPath.mkdirs()
                    val file = File(pdfDirPath, "TrollText.pdf")
                    val contentUri: Uri =
                        FileProvider.getUriForFile(
                            activity,
                            "org.scotthamilton.trollslate.fileprovider",
                            file
                        )
                    FileOutputStream(file).let {
                        drawLettersToPdf(
                            text,
                            angle,
                            strokeWidth = strokeWidth,
                            out = it,
                            letter_size = letterSize
                        )
                        it.close()
                    }
                    activity.startActivity(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                activity.getString(R.string.intent_pdf_share_subject)
                            )
                            putExtra(Intent.EXTRA_STREAM, contentUri)
                        }
                    )
                } catch (e: IOException) {
                    println("[error] can't generate pdf file: $e")
                }
            }
        },
        modifier = modifier.padding(end = 15.dp, top = 15.dp).width(60.dp).height(60.dp),
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Sharp.Share,
            "",
            modifier =
                Modifier.fillMaxSize(0.5f)
                    .background(Color.Transparent),
            tint = md_theme_light_secondary
        )
    }
}

@Composable
private fun angleToLetterSize(angle: Float): Size {
    val normalRatio = 1f / 4f
    val projectedRatio = normalRatio * sin(angle * PI / 180f)
    return (LocalConfiguration.current.screenHeightDp * projectedRatio).let {
        if (it > 50f) {
            Size(50f, 50f / projectedRatio.toFloat())
        } else {
            Size(it.toFloat(), LocalConfiguration.current.screenHeightDp.toFloat())
        }
    }
}

private fun letterSizeToStokeWidth(letterSize: Size): Float = letterSize.width * 0.2f

@RequiresApi(Build.VERSION_CODES.N)
@Preview(showBackground = true)
@Composable
fun TrollContent(text: String = "DORIAN",
                 angle: Float = 5f,
                 activity: Activity? = null,
                 colorScheme: ColorScheme = MaterialTheme.colorScheme) {
    val letterSize = angleToLetterSize(angle)
    val strokeWidth = letterSizeToStokeWidth(letterSize)
    TrollslateTheme {
        Surface(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyRow(
                    modifier =
                        Modifier.background(Color.White)
                            .fillMaxSize()
                            .padding(20.dp)
                            .testTag("trollActivityLazyRow"),
                    horizontalArrangement = Arrangement.spacedBy((letterSize.width * 0.4f).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(1) { Spacer(modifier = Modifier.fillMaxHeight().width(strokeWidth.dp)) }
                    items(text.length) { index ->
                        Letter(
                            modifier =
                                Modifier.background(Color.White)
                                    .width(letterSize.width.dp)
                                    .height(letterSize.height.dp),
                            letter = text.elementAt(index),
                            backgroundColor = Color.White,
                            textColor = Color.Black,
                            strokeWidth = strokeWidth,
                            angle = angle
                        )
                    }
                    items(1) { Spacer(modifier = Modifier.fillMaxHeight().width(100.dp)) }
                }
                TrollShareFab(
                    modifier = Modifier.align(Alignment.TopEnd),
                    activity = activity,
                    text = text,
                    angle = angle * 0.75f,
                    colorScheme = colorScheme
                )
            }
        }
    }
}
