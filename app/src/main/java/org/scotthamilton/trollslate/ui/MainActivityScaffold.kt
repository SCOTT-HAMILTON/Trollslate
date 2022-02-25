package org.scotthamilton.trollslate.ui

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.scotthamilton.trollslate.utils.rollToAcceptableAngle
import org.scotthamilton.trollslate.utils.rotationVectorToRollAngle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityScaffold(navController: NavController?, activity: Activity?) {
    val sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
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
            SensorManager.SENSOR_DELAY_UI
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
                MainActivityContent(
                    phoneAngleSelectorData = phoneAngleSelectorData,
                    trollTextFieldData = trollTextFieldData,
                    navController = navController,
                    activity = activity
                )
            }
        )
    }
}
