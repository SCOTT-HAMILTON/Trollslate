package org.scotthamilton.trollslate

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.github.pwittchen.reactivesensors.library.SensorNotFoundException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.scotthamilton.trollslate.ui.*
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme


fun rotationVectorToRollAngle(rotationVector: FloatArray): Float {
    val rotationMatrix = FloatArray(9)
    SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
    val worldAxisForDeviceAxisX = SensorManager.AXIS_Y
    val worldAxisForDeviceAxisY = SensorManager.AXIS_X

    val adjustedRotationMatrix = FloatArray(9)
    SensorManager.remapCoordinateSystem(
        rotationMatrix, worldAxisForDeviceAxisX,
        worldAxisForDeviceAxisY, adjustedRotationMatrix
    )
    val orientation = FloatArray(3)
    SensorManager.getOrientation(adjustedRotationMatrix, orientation)
    return orientation[2] * -57 // roll
}

private fun rollTOAcceptableAngle(rollAngle: Float, maxAngle: Float, minAngle: Float) : Float {
    val middle = (minAngle+maxAngle)/2f
    val oppositMiddle = -middle
    val rollMax = 180f-maxAngle
    val rollMin = 180f-minAngle
    return if (rollAngle in rollMax..rollMin) {
        180f - rollAngle
    } else {
        if (180 >= rollAngle && rollAngle >= 180f-minAngle) {
            minAngle
        } else if (rollAngle > oppositMiddle) maxAngle else minAngle
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sensors = ReactiveSensors(applicationContext)

        setContent {
            TrollslateTheme {
                val gyroscopeMissing = remember {
                    mutableStateOf(!sensors.hasSensor(Sensor.TYPE_ROTATION_VECTOR))
                }
                val phoneAngleSelectorData = defaultPhoneAngleSelectorData()
                if (!gyroscopeMissing.value) {
                    ReactiveSensors(applicationContext).observeSensor(Sensor.TYPE_ROTATION_VECTOR)
                        .subscribeOn(Schedulers.computation())
                        .filter { obj: ReactiveSensorEvent -> obj.sensorChanged() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            val roll = rotationVectorToRollAngle(it.sensorValues())
                            val angle = rollTOAcceptableAngle(roll,
                                phoneAngleSelectorData.angleRange.last.toFloat(),
                                phoneAngleSelectorData.angleRange.first.toFloat())
                            phoneAngleSelectorData.currentAngle.value = angle
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
//
                    val snackBarHostState = remember { SnackbarHostState() }
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackBarHostState, snackbar = {
                                Snackbar(
                                    snackbarData = it,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            })
                        },
                        topBar = {},
                        content = {
                            if (gyroscopeMissing.value) {
                                LaunchedEffect(snackBarHostState) {
                                    snackBarHostState.showSnackbar(
                                        "Impossible d'accéder au gyroscope, la fonctionnalité ne sera pas disponible"
                                    )
                                }
                            } else {
                                LaunchedEffect(snackBarHostState) {
                                    snackBarHostState.showSnackbar(
                                        "Le gyroscope est accessible !"
                                    )
                                }
                            }
                            Main(phoneAngleSelectorData = phoneAngleSelectorData)
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(value = 26)
@Preview(showBackground = true)
@Composable
fun Main(phoneAngleSelectorData: PhoneAngleSelectorData = defaultPhoneAngleSelectorData()) {
    TrollslateTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                LettersSlide()
                TrollTextField()
                PhoneAngleSelector(phoneAngleSelectorData)
            }
        }
    }
}
