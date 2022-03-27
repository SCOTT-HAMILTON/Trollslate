package org.scotthamilton.trollslate.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.scotthamilton.trollslate.data.FontData

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun LettersSlide(colorScheme: ColorScheme) {
    // drop the space character
    val letters = FontData.lettersCodonTable.keys.drop(1)
    Box(
        modifier =
            Modifier.height(150.dp)
                .width(300.dp)
                .background(
                    color = colorScheme.primaryContainer,
                    shape = RoundedCornerShape(20)
                ),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier =
                Modifier.background(colorScheme.primaryContainer)
                    .fillMaxSize(0.8f)
                    .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(letters.count()) { index ->
                Letter(
                    modifier =
                        Modifier.background(colorScheme.surface)
                            .fillParentMaxHeight()
                            .width(50.dp),
                    letter = letters.elementAt(index),
                    backgroundColor = colorScheme.primaryContainer,
                    textColor = colorScheme.onPrimaryContainer,
                    angle = 30f,
                    adaptiveStrokes = true
                )
            }
        }
    }
}
