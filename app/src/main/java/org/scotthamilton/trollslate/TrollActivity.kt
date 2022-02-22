package org.scotthamilton.trollslate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin
import org.scotthamilton.trollslate.data.IntentData.Companion.PHONE_ANGLE_INTENT_EXTRA_KEY
import org.scotthamilton.trollslate.data.IntentData.Companion.TROLL_TEXT_INTENT_EXTRA_KEY
import org.scotthamilton.trollslate.ui.Letter
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme

class TrollActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrollslateTheme {
                Main(
                    intent.extras?.get(TROLL_TEXT_INTENT_EXTRA_KEY) as String,
                    intent.extras?.get(PHONE_ANGLE_INTENT_EXTRA_KEY) as Float
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview(showBackground = true)
@Composable
private fun Main(text: String = "DORIAN", angle: Float = 10f) {
    val normalRatio = 1f / 4f
    val projectedRatio = normalRatio * sin(angle * PI / 180f)
    TrollslateTheme {
        Surface(modifier = Modifier.fillMaxSize().background(Color.White)) {
            LazyRow(
                modifier = Modifier.background(Color.White).fillMaxSize().padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(text.length) { index ->
                    val (letterWidth, letterHeight) =
                        (LocalConfiguration.current.screenHeightDp * projectedRatio).let {
                            if (it > 50f) {
                                50f to 50f / projectedRatio.toFloat()
                            } else {
                                it.toFloat() to LocalConfiguration.current.screenHeightDp.toFloat()
                            }
                        }
                    Letter(
                        modifier =
                            Modifier.background(Color.White)
                                .width(letterWidth.dp)
                                .height(letterHeight.dp),
                        letter = text.elementAt(index),
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        strokeWidth = letterWidth * 0.2f,
                        padding = letterWidth * 0.2f,
                        angle = angle
                    )
                }
            }
        }
    }
}
