package org.scotthamilton.trollslate.ui

import android.app.Activity
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme

@RequiresApi(value = 26)
@Preview(showBackground = true)
@Composable
fun MainActivityContent(
    phoneAngleSelectorData: PhoneAngleSelectorData = defaultPhoneAngleSelectorData(),
    trollTextFieldData: TrollTextFieldData = defaultTrollTextFieldData(),
    navController: NavController? = null,
    activity: Activity? = null
) {
    TrollslateTheme {
        Surface(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                val creditState = remember {
                    MutableTransitionState(true).apply { targetState = true }
                }
                val trollFabState = remember {
                    MutableTransitionState(false).apply { targetState = false }
                }
                Box(modifier = Modifier.align(Alignment.End)) {
                    TrollFab(
                        trollTextFieldData = trollTextFieldData,
                        phoneAngleSelectorData = phoneAngleSelectorData,
                        navController = navController,
                        state = trollFabState,
                    )
                    CreditsButton(creditState, navController, activity)
                }
                Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)
                ) {
                    LettersSlide()
                    TrollTextField(trollTextFieldData) {
                        withContext(Dispatchers.Main) {
                            creditState.targetState = false
                            trollFabState.targetState = true
                        }
                    }
                    PhoneAngleSelector(phoneAngleSelectorData)
                }
            }
        }
    }
}
