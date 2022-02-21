package org.scotthamilton.trollslate.ui

import android.graphics.Camera
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.util.SizeF
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import org.scotthamilton.trollslate.data.FontData

@RequiresApi(Build.VERSION_CODES.N)
fun modern3DPhonePath(phoneDims: SizeF): Path {
    val bottomStripFrac = 0.1f
    val topStripFrac = 0.1f
    val bottomButtonSizeFrac = SizeF(0.05f, 0.24f)
    val topNotchSizeFrac = SizeF(0.015f, 0.2f)
    return Path().apply {
        CornerRadius(phoneDims.width * 0.05f, phoneDims.width * 0.05f).let { radius ->
            addRoundRect(
                RoundRect(0f, 0f, phoneDims.width, phoneDims.height, radius, radius, radius, radius)
            )
        }
        addPath(
            Path().apply {
                addRect(
                    Rect(
                        phoneDims.width * bottomStripFrac,
                        phoneDims.height * 0.08f,
                        phoneDims.width * (1f - topStripFrac),
                        phoneDims.height * 0.92f
                    )
                )
            }
        )
        addPath(
            Path().apply {
                val radius = CornerRadius(phoneDims.width * 0.02f, phoneDims.width * 0.02f)
                val width = bottomButtonSizeFrac.width * phoneDims.width
                val height = bottomButtonSizeFrac.height * phoneDims.height
                val bottomStripWidth = bottomStripFrac * phoneDims.width
                addRoundRect(
                    RoundRect(
                        bottomStripWidth / 2f - width / 2f,
                        phoneDims.height / 2f - height / 2f,
                        bottomStripWidth / 2f + width / 2f,
                        phoneDims.height / 2f + height / 2f,
                        radius,
                        radius,
                        radius,
                        radius
                    )
                )
            }
        )
        addPath(
            Path().apply {
                val radius = CornerRadius(phoneDims.width * 0.02f, phoneDims.width * 0.02f)
                val width = topNotchSizeFrac.width * phoneDims.width
                val height = topNotchSizeFrac.height * phoneDims.height
                val topStripWidth = topStripFrac * phoneDims.width
                addRoundRect(
                    RoundRect(
                        phoneDims.width - topStripWidth * 0.4f - width / 2f,
                        phoneDims.height / 2f - height / 2f,
                        phoneDims.width - topStripWidth * 0.4f + width / 2f,
                        phoneDims.height / 2f + height / 2f,
                        radius,
                        radius,
                        radius,
                        radius
                    )
                )
            }
        )
        FontData.lettersCodonTable['A']?.let { codon ->
            val size = Size(phoneDims.height * 0.3f, phoneDims.width * 0.3f)
            codon2LetterPath(codon, size)?.let {
                val rotatedPath =
                    it.asAndroidPath()
                        .apply {
                            transform(Matrix().also { matrix -> matrix.setRotate(90f, 0f, 0f) })
                        }
                        .asComposePath()
                val width = rotatedPath.getBounds().width
                val height = rotatedPath.getBounds().height
                addPath(
                    rotatedPath,
                    Offset(phoneDims.width * 1.0f - width, phoneDims.height / 2f - height / 2f)
                )
            }
        }
    }
}

@RequiresApi(value = 26)
@Composable
fun Canvas3DPhone(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    angle: Float
) {
    Canvas(
        modifier = modifier.background(backgroundColor),
        onDraw = {
            val ratio = size.width / size.height
            val phoneDims =
                if (ratio > 2.0f) {
                    SizeF(size.height * 2f, size.height)
                } else {
                    SizeF(size.width, size.width / 2f)
                }
            val phonePath = modern3DPhonePath(phoneDims)
            drawIntoCanvas { canvas ->
                val matrix = Matrix()
                Camera()
                    .apply {
                        rotateX(68f)
                        rotateY(-angle)
                        translate(-150f, 0f, 100f)
                    }
                    .getMatrix(matrix)
                canvas.nativeCanvas.concat(matrix)
                canvas.nativeCanvas.drawPath(
                    phonePath.asAndroidPath(),
                    Paint().apply {
                        color = onBackgroundColor.toArgb()
                        strokeWidth = size.width * 0.009f
                        style = Paint.Style.STROKE
                    }
                )
            }
        }
    )
}
