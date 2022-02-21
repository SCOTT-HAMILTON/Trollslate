package org.scotthamilton.trollslate.utils

import android.hardware.SensorManager

fun rotationVectorToRollAngle(rotationVector: FloatArray): Float {
    val rotationMatrix = FloatArray(9)
    SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
    val worldAxisForDeviceAxisX = SensorManager.AXIS_Y
    val worldAxisForDeviceAxisY = SensorManager.AXIS_X

    val adjustedRotationMatrix = FloatArray(9)
    SensorManager.remapCoordinateSystem(
        rotationMatrix,
        worldAxisForDeviceAxisX,
        worldAxisForDeviceAxisY,
        adjustedRotationMatrix
    )
    val orientation = FloatArray(3)
    SensorManager.getOrientation(adjustedRotationMatrix, orientation)
    return orientation[2] * -57 // roll
}

fun rollToAcceptableAngle(rollAngle: Float, maxAngle: Float, minAngle: Float): Float {
    val middle = (minAngle + maxAngle) / 2f
    val oppositMiddle = -middle
    val rollMax = 180f - maxAngle
    val rollMin = 180f - minAngle
    return if (rollAngle in rollMax..rollMin) {
        180f - rollAngle
    } else {
        if (180 >= rollAngle && rollAngle >= 180f - minAngle) {
            minAngle
        } else if (rollAngle > oppositMiddle) maxAngle else minAngle
    }
}
