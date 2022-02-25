package org.scotthamilton.trollslate.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.scotthamilton.trollslate.data.toUrlBase64

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrollFab(
    modifier: Modifier = Modifier,
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
        FloatingActionButton(
            onClick = {
                if (!trollTextFieldData.showError.value) {
                    val text = trollTextFieldData.text.value.toUrlBase64()
                    val angle = phoneAngleSelectorData.currentAngle.value
                    navController?.navigate(route = "troll?text=$text&angle=$angle")
                }
            },
            modifier = modifier.padding(end = 20.dp, top = 20.dp).width(60.dp).height(60.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Sharp.Check,
                "",
                modifier =
                    Modifier.fillMaxSize()
                        .background(
                            if (!trollTextFieldData.showError.value)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
            )
        }
    }
}
