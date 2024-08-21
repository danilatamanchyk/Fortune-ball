package com.example.fortuneball

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

class ShakeDetectorViewModel : ViewModel() {

    private val _needChangeImage = MutableLiveData<Boolean>(false)
    val needChangeImage: LiveData<Boolean> get() = _needChangeImage
    lateinit var application: Application

    private var sensorManager: SensorManager? = null
    private var cleanAcceleration: Float = 0f
    private var lastCleanAcceleration: Float = 0f
    private var shakeTimeout = 1000
    private var lastShakeTime: Long = 0

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            CoroutineScope(Dispatchers.IO).launch {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                lastCleanAcceleration = cleanAcceleration
                cleanAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH
                val delta: Float = abs(cleanAcceleration - lastCleanAcceleration)

                if (5 < delta || delta < -5) {
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTime >= shakeTimeout) {
                        _needChangeImage.postValue(true)
                        lastShakeTime = now
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    fun resetNeedChangeImage() {
        _needChangeImage.value = false
    }

    fun registerSensorListener() {
        sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(sensorListener, sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun unregisterSensorListener() {
        sensorManager?.unregisterListener(sensorListener)
    }
}