package org.scotthamilton.trollslate.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.scotthamilton.trollslate.utils.toUrlBase64

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrollFab(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    trollTextFieldData: TrollTextFieldData,
    phoneAngleSelectorData: PhoneAngleSelectorData,
    navController: NavController? = null,
    state: MutableTransitionState<Boolean>
) {
    AnimatedVisibility(
        visibleState = state,
        enter =
            scaleIn(
                animationSpec = tween(2000, easing = CubicBezierEasing(0.71f, -0.06f, 0.00f, 0.99f))
            )
    ) {
        OutlinedFloatingActionButton(
            colorScheme = colorScheme,
            onClick = {
                if (!trollTextFieldData.showError.value) {
                    val text = trollTextFieldData.text.value.toUrlBase64()
                    val angle = phoneAngleSelectorData.currentAngle.value
                    navController?.navigate(route = "troll?text=$text&angle=$angle")
                }
            },
            modifier =
                modifier
                    .padding(end = 20.dp, top = 20.dp)
                    .width(60.dp)
                    .height(60.dp)
                    .background(Color.Transparent)
                    .testTag("trollFab"),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Sharp.Check,
                "",
                modifier =
                    Modifier.fillMaxSize(0.5f)
                        .background(Color.Transparent, shape = RectangleShape),
                tint = colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun OutlinedFloatingActionButton(
    colorScheme: ColorScheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(16.0.dp),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .border(BorderStroke(2.dp, colorScheme.onSurface), shape = shape)
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
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = 56.dp,
                            minHeight = 56.dp,
                        ).background(Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) { content() }
            }
        }
    }
}
