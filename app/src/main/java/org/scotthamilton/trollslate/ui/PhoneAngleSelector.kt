package org.scotthamilton.trollslate.ui

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
import androidx.compose.ui.unit.dp

private fun ilerp(from: IntRange, to: IntRange, value: Int) =
    (value - from.first).toFloat()*to.count().toFloat()/from.count().toFloat()+to.first.toFloat()

@RequiresApi(value = 26)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhoneAngleSelector() {
    var scrollOffset by remember { mutableStateOf(0f) }
    val scrollRange = IntRange(-1000, 1000)
    val angleRange = IntRange(10, 80)
    Box (modifier =
    Modifier
        .height(200.dp)
        .width(300.dp)
        .background(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(20)
        )
        .scrollable(
            orientation = Orientation.Vertical,
            state = rememberScrollableState { delta ->
                val new_offset = scrollOffset - delta
                if (scrollRange.first < new_offset && new_offset < scrollRange.last) {
                    scrollOffset = new_offset
                }
                delta
            }
        ),
        contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas3DPhone(
                modifier = Modifier
                    .size(150.dp, 75.dp)
                    .padding(start = 10.dp),
                backgroundColor =
                MaterialTheme.colorScheme.secondaryContainer,
                angle = ilerp(
                    scrollRange,
                    angleRange,
                    scrollOffset.toInt()
                )
            )
        }
    }
}