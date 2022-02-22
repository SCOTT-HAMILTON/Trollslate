package org.scotthamilton.trollslate.ui

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scotthamilton.trollslate.R

data class PhoneAngleSelectorData(
    val angleRange: IntRange,
    val currentAngle: MutableState<Float>,
    val useGyroscope: MutableState<Boolean>
)

fun defaultPhoneAngleSelectorData(): PhoneAngleSelectorData =
    PhoneAngleSelectorData(
        angleRange = IntRange(10, 80),
        currentAngle = mutableStateOf(80f),
        useGyroscope = mutableStateOf(false)
    )

private fun ilerp(from: IntRange, to: IntRange, value: Int) =
    (value - from.first).toFloat() * to.count().toFloat() / from.count().toFloat() +
        to.first.toFloat()

@RequiresApi(value = 26)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhoneAngleSelector(data: PhoneAngleSelectorData) {
    val scrollRange = IntRange(-1000, 1000)
    var currentScrollOffset = ilerp(data.angleRange, scrollRange, data.currentAngle.value.toInt())
    Box(
        modifier =
            Modifier.height(200.dp)
                .width(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(20)
                )
                .scrollable(
                    orientation = Orientation.Vertical,
                    state =
                        rememberScrollableState { delta ->
                            val newOffset = currentScrollOffset - delta
                            if (scrollRange.first < newOffset && newOffset < scrollRange.last) {
                                currentScrollOffset = newOffset
                                data.currentAngle.value =
                                    ilerp(scrollRange, data.angleRange, currentScrollOffset.toInt())
                            }
                            delta
                        }
                ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
            Text(
                text = "${"%.2f".format(data.currentAngle.value)}°",
                modifier = Modifier.align(Alignment.Start).padding(start = 30.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(40.dp))
            Canvas3DPhone(
                modifier = Modifier.size(120.dp, 60.dp).padding(start = 10.dp),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                angle = data.currentAngle.value
            )
        }

        val fabDisabledColor = MaterialTheme.colorScheme.surface
        val fabEnabledColor = MaterialTheme.colorScheme.primary
        FloatingActionButton(
            modifier =
                Modifier.size(60.dp).align(Alignment.TopEnd).padding(end = 20.dp, top = 20.dp),
            onClick = { data.useGyroscope.value = !data.useGyroscope.value }
        ) {
            Icon(
                painterResource(id = R.mipmap.gyroscope_foreground),
                "",
                modifier =
                    Modifier.fillMaxSize()
                        .background(
                            if (data.useGyroscope.value) fabEnabledColor else fabDisabledColor
                        )
            )
        }
    }
}
