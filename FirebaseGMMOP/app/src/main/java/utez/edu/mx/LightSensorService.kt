package utez.edu.mx

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.provider.Settings
import android.util.Log

class LightSensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()
        // Inicializa el SensorManager y el sensor de luz
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor == null) {
            Log.e("LightSensorService", "Sensor de luz no disponible en este dispositivo")
            stopSelf() // Termina el servicio si no hay sensor
        } else {
            // Registra el sensor de luz
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Mantén el servicio en ejecución
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Desregistra el sensor cuando el servicio se destruye
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        // No implementamos comunicación para un servicio sin conexión
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightValue = event.values[0] // Nivel de luz en lux
            adjustScreenBrightness(lightValue)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita manejar cambios de precisión para este caso
    }

    private fun adjustScreenBrightness(lightValue: Float) {
        val brightness = when {
            lightValue < 10 -> 0.1f // Oscuridad
            lightValue < 500 -> 0.5f // Luz moderada
            else -> 1.0f // Luz brillante
        }

        try {
            if (Settings.System.canWrite(applicationContext)) {
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    (brightness * 255).toInt()
                )
                Log.d("LightSensorService", "Brillo ajustado: $brightness")
            } else {
                Log.e("LightSensorService", "Permiso para escribir en ajustes no concedido")
            }
        } catch (e: Exception) {
            Log.e("LightSensorService", "Error al ajustar el brillo: ${e.message}")
        }
    }
}
