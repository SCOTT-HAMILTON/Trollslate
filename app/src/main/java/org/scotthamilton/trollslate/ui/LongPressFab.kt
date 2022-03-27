package org.scotthamilton.trollslate.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LongPressFab(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(12.0.dp),
    colorScheme: ColorScheme,
    containerColor: Color = colorScheme.primaryContainer,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier =
            modifier.combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        interactionSource = interactionSource,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = elevation.tonalElevation(interactionSource = interactionSource).value,
        shadowElevation = elevation.shadowElevation(interactionSource = interactionSource).value,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            // Adding the text style from [ExtendedFloatingActionButton] to all FAB variations. In
            // the majority of cases this will have no impact, because icons are expected, but if a
            // developer decides to put some short text to emulate an icon, (like "?") then it will
            // have the correct styling.
            ProvideTextStyle(
                MaterialTheme.typography.labelLarge,
            ) {
                Box(
                    modifier =
                        Modifier.defaultMinSize(
                            minWidth = 56.0.dp,
                            minHeight = 56.0.dp,
                        ),
                    contentAlignment = Alignment.Center,
                ) { content() }
            }
        }
    }
}
