package org.scotthamilton.trollslate

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager.SENSOR_DELAY_UI
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.github.pwittchen.reactivesensors.library.SensorNotFoundException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
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

        val sensors = ReactiveSensors(applicationContext)

        setContent {
            TrollslateTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val gyroscopeMissing = remember {
                    mutableStateOf(!sensors.hasSensor(Sensor.TYPE_ROTATION_VECTOR))
                }
                val phoneAngleSelectorData =
                    defaultPhoneAngleSelectorData(gyroscopeMissing, snackBarHostState)
                val trollTextFieldData = defaultTrollTextFieldData()
                if (!gyroscopeMissing.value) {
                    ReactiveSensors(applicationContext)
                        .observeSensor(Sensor.TYPE_ROTATION_VECTOR, SENSOR_DELAY_UI)
                        .subscribeOn(Schedulers.computation())
                        .filter { obj: ReactiveSensorEvent -> obj.sensorChanged() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (phoneAngleSelectorData.useGyroscope.value) {
                                val roll = rotationVectorToRollAngle(it.sensorValues())
                                val angle =
                                    rollToAcceptableAngle(
                                        roll,
                                        phoneAngleSelectorData.angleRange.last.toFloat(),
                                        phoneAngleSelectorData.angleRange.first.toFloat()
                                    )
                                phoneAngleSelectorData.currentAngle.value = angle
                            }
                        }) { throwable ->
                            if (throwable is SensorNotFoundException) {
                                gyroscopeMissing.value = true
                            }
                        }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                                activity = this
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrollFab(
    modifier: Modifier,
    trollTextFieldData: TrollTextFieldData,
    phoneAngleSelectorData: PhoneAngleSelectorData,
    activity: Activity?
) {
    FloatingActionButton(
        onClick = {
            if (!trollTextFieldData.showError.value) {
                println(
                    "Launching activity with text `${trollTextFieldData.text}`, ${phoneAngleSelectorData.currentAngle}"
                )
                activity?.startActivity(
                    Intent(activity, TrollActivity::class.java).apply {
                        putExtra(TROLL_TEXT_INTENT_EXTRA_KEY, trollTextFieldData.text.value)
                        putExtra(
                            PHONE_ANGLE_INTENT_EXTRA_KEY,
                            phoneAngleSelectorData.currentAngle.value
                        )
                    }
                )
                activity?.finish()
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
                        if (!trollTextFieldData.showError.value) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
        )
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
                TrollFab(
                    modifier = Modifier.align(Alignment.End),
                    trollTextFieldData = trollTextFieldData,
                    phoneAngleSelectorData = phoneAngleSelectorData,
                    activity
                )
                Spacer(modifier = Modifier.fillMaxWidth().height(20.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)
                ) {
                    LettersSlide()
                    TrollTextField(trollTextFieldData)
                    PhoneAngleSelector(phoneAngleSelectorData)
                }
            }
        }
    }
}
