package org.scotthamilton.trollslate.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.scotthamilton.trollslate.R
import org.scotthamilton.trollslate.utils.toUrlBase64

@Composable
fun CreditsButton(
    state: MutableTransitionState<Boolean>,
    navController: NavController? = null,
    activity: Activity?
) {
    AnimatedVisibility(
        visibleState = state,
        exit = fadeOut(animationSpec = tween(durationMillis = 10))
    ) {
        OutlinedButton(
            onClick = {
                activity?.getString(R.string.credits_text)?.let {
                    val text = it.trimIndent().toUrlBase64()
                    navController?.navigate(route = "troll?text=$text&angle=5.0&navbackable=true")
                }
            },
            Modifier.padding(top = 10.dp, end = 10.dp).testTag("creditsButton"),
        ) {
            Text(
                text = stringResource(id = R.string.credits),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
