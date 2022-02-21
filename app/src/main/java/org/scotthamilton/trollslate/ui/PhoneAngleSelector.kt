package org.scotthamilton.trollslate.ui

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data class PhoneAngleSelectorData(
    val angleRange: IntRange,
    val currentAngle: MutableState<Float>
)

fun defaultPhoneAngleSelectorData() : PhoneAngleSelectorData =
    PhoneAngleSelectorData(
        angleRange = IntRange(10, 80),
        currentAngle = mutableStateOf(45f)
    )

private fun ilerp(from: IntRange, to: IntRange, value: Int) =
    (value - from.first).toFloat() * to.count().toFloat() / from.count().toFloat() +
        to.first.toFloat()

@RequiresApi(value = 26)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhoneAngleSelector(data: PhoneAngleSelectorData) {
    val scrollRange = IntRange(-1000, 1000)
    var scrollOffset by remember {
        mutableStateOf(ilerp(data.angleRange, scrollRange, data.currentAngle.value.toInt()))
    }
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
                            val newOffset = scrollOffset - delta
                            if (scrollRange.first < newOffset && newOffset < scrollRange.last) {
                                scrollOffset = newOffset
                                data.currentAngle.value =
                                    ilerp(scrollRange, data.angleRange, scrollOffset.toInt())
                            }
                            delta
                        }
                ),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas3DPhone(
                modifier = Modifier.size(150.dp, 75.dp).padding(start = 10.dp),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                angle = data.currentAngle.value
            )
        }
    }
}
