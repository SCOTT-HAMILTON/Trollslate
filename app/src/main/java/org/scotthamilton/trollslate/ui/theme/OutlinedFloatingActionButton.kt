package org.scotthamilton.trollslate.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedFloatingActionButton(
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(16.0.dp),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier =
            modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick
                )
                .border(BorderStroke(2.dp, color), shape = shape)
                .background(Color.Transparent),
        interactionSource = interactionSource,
        shape = shape,
        color = Color.Transparent,
        contentColor = Color.Transparent,
        tonalElevation = elevation.tonalElevation(interactionSource = interactionSource).value,
        // no shadow elevation otherwise the content is not transparent
        ) {
        CompositionLocalProvider(LocalContentColor provides Color.Transparent) {
            ProvideTextStyle(
                MaterialTheme.typography.labelLarge,
            ) {
                Box(
                    modifier =
                        Modifier.defaultMinSize(
                                minWidth = 56.dp,
                                minHeight = 56.dp,
                            )
                            .background(Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) { content() }
            }
        }
    }
}
