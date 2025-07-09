package io.github.luishenriqueaguiar.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.luishenriqueaguiar.domain.model.FocusViolation
import io.github.luishenriqueaguiar.domain.repository.SensorRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class SensorRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SensorRepository {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    override val focusViolations: Flow<FocusViolation> = callbackFlow {
        val listener = object : SensorEventListener {
            private var lastX: Float? = null
            private var lastY: Float? = null
            private var lastZ: Float? = null
            private val moveThreshold = 1.5f

            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return

                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        val x = event.values[0]; val y = event.values[1]; val z = event.values[2]
                        if (lastX != null) {
                            val deltaX = abs(lastX!! - x); val deltaY = abs(lastY!! - y); val deltaZ = abs(lastZ!! - z)
                            if (deltaX > moveThreshold || deltaY > moveThreshold || deltaZ > moveThreshold) {
                                trySend(FocusViolation.PHONE_MOVED)
                            }
                        }
                        lastX = x; lastY = y; lastZ = z
                    }
                    Sensor.TYPE_PROXIMITY -> {
                        // O valor 0.0 geralmente significa que o sensor está coberto (virado para baixo)
                        if (event.values[0] == 0.0f) {
                            trySend(FocusViolation.PHONE_PLACED_FACE_DOWN)
                        } else {
                            trySend(FocusViolation.PHONE_FLIPPED_UP)
                        }
                    }
                }
            }
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose { sensorManager.unregisterListener(listener) }
    }

    override fun startMonitoring() {}
    override fun stopMonitoring() {}
}