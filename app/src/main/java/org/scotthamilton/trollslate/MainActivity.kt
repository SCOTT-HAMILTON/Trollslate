package org.scotthamilton.trollslate

import android.app.Activity
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_UI
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.scotthamilton.trollslate.data.IntentData.Companion.PHONE_ANGLE_INTENT_EXTRA_KEY
import org.scotthamilton.trollslate.data.IntentData.Companion.TROLL_TEXT_INTENT_EXTRA_KEY
import org.scotthamilton.trollslate.ui.*
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme
import org.scotthamilton.trollslate.utils.rollToAcceptableAngle
import org.scotthamilton.trollslate.utils.rotationVectorToRollAngle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TrollslateTheme { MainActivityContent(this) } }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityContent(activity: Activity?) {
    val sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager?
    val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    val snackBarHostState = remember { SnackbarHostState() }
    val gyroscopeMissing = remember { mutableStateOf(rotationSensor == null) }
    val phoneAngleSelectorData =
        defaultPhoneAngleSelectorData(activity, gyroscopeMissing, snackBarHostState)
    val trollTextFieldData = defaultTrollTextFieldData()
    if (!gyroscopeMissing.value) {
        sensorManager?.registerListener(
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.values?.let {
                        if (phoneAngleSelectorData.useGyroscope.value) {
                            val roll = rotationVectorToRollAngle(it)
                            val angle =
                                rollToAcceptableAngle(
                                    roll,
                                    phoneAngleSelectorData.angleRange.last.toFloat(),
                                    phoneAngleSelectorData.angleRange.first.toFloat()
                                )
                            phoneAngleSelectorData.currentAngle.value = angle
                        }
                    }
                }
                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
            },
            rotationSensor,
            SENSOR_DELAY_UI
        )
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    snackbar = {
                        Snackbar(
                            snackbarData = it,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            },
            topBar = {},
            floatingActionButtonPosition = FabPosition.Center,
            content = {
                Main(
                    phoneAngleSelectorData = phoneAngleSelectorData,
                    trollTextFieldData = trollTextFieldData,
                    activity = activity
                )
            }
        )
    }
}

fun launchTrollActivityAndExit(activity: Activity?, text: String, angle: Float) {
    println("Launching activity with text `$text`, $angle")
    activity?.startActivity(
        Intent(activity, TrollActivity::class.java).apply {
            putExtra(TROLL_TEXT_INTENT_EXTRA_KEY, text)
            putExtra(PHONE_ANGLE_INTENT_EXTRA_KEY, angle)
        }
    )
    activity?.finish()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrollFab(
    modifier: Modifier = Modifier,
    trollTextFieldData: TrollTextFieldData,
    phoneAngleSelectorData: PhoneAngleSelectorData,
    activity: Activity?,
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
                    launchTrollActivityAndExit(
                        activity,
                        trollTextFieldData.text.value,
                        phoneAngleSelectorData.currentAngle.value
                    )
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

@Composable
fun CreditsButton(state: MutableTransitionState<Boolean>, activity: Activity?) {
    AnimatedVisibility(
        visibleState = state,
        exit = fadeOut(animationSpec = tween(durationMillis = 10))
    ) {
        OutlinedButton(
            onClick = {
                activity?.getString(R.string.credits_text)?.let {
                    launchTrollActivityAndExit(
                        activity,
                        it.trimIndent().replace(Regex("""\n*"""), " "),
                        10f
                    )
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

@RequiresApi(value = 26)
@Preview(showBackground = true)
@Composable
private fun Main(
    phoneAngleSelectorData: PhoneAngleSelectorData = defaultPhoneAngleSelectorData(),
    trollTextFieldData: TrollTextFieldData = defaultTrollTextFieldData(),
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
                        activity = activity,
                        state = trollFabState
                    )
                    CreditsButton(creditState, activity)
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
