package com.example.andro_apestor_cmm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlin.math.sqrt

class SensoresActivity : AppCompatActivity(), SensorEventListener, LocationListener {


    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private lateinit var tvAcelerometro: TextView
    private lateinit var tvEstadoMovimiento: TextView


    private lateinit var locationManager: LocationManager
    private lateinit var tvGps: TextView


    private val pedirPermisoUbicacion = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            iniciarGps()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensores)

        tvAcelerometro = findViewById(R.id.tvAcelerometro)
        tvEstadoMovimiento = findViewById(R.id.tvEstadoMovimiento)
        tvGps = findViewById(R.id.tvGps)

        val btnObtenerGps = findViewById<Button>(R.id.btnObtenerGps)
        val btnVolverMenu = findViewById<Button>(R.id.btnVolverMenu)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (acelerometro == null) {
            tvAcelerometro.text = "Este dispositivo no tiene acelerómetro."
        }


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnObtenerGps.setOnClickListener {
            verificarPermisoYArrancarGps()
        }

        btnVolverMenu.setOnClickListener {
            finish()
        }


        verificarPermisoYArrancarGps()
    }


    override fun onResume() {
        super.onResume()
        acelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            tvAcelerometro.text = String.format(
                Locale.US,
                "X (Lateral): %.2f m/s²\nY (Vertical): %.2f m/s²\nZ (Profundidad): %.2f m/s²",
                x, y, z
            )

            // Detectar si el teléfono está quieto (~9.8 m/s² por la gravedad) o sacudiéndose
            val fuerzaTotal = sqrt(x * x + y * y + z * z)
            tvEstadoMovimiento.text = when {
                fuerzaTotal > 14.0 -> "Estado: ¡Agitando el teléfono!"
                fuerzaTotal in 8.5..11.0 -> "Estado: Teléfono quieto"
                else -> "Estado: En movimiento"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    private fun verificarPermisoYArrancarGps() {
        val tienePermisoFino = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermisoFino) {
            iniciarGps()
        } else {
            pedirPermisoUbicacion.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun iniciarGps() {
        try {
            val gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val redActiva = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!gpsActivo && !redActiva) {
                tvGps.text = "Activa la Ubicación (GPS) en la barra superior del teléfono."
                return
            }

            tvGps.text = "Buscando señal satelital..."


            val ultimaUbicacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ultimaUbicacion?.let { actualizarTextoUbicacion(it) }


            if (gpsActivo) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 1f, this)
            }
            if (redActiva) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 1f, this)
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Sin permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLocationChanged(location: Location) {
        actualizarTextoUbicacion(location)
    }

    private fun actualizarTextoUbicacion(location: Location) {
        tvGps.text = String.format(
            Locale.US,
            "Latitud: %.6f\nLongitud: %.6f\nAltitud: %.1f m\nPrecisión: %.1f m",
            location.latitude,
            location.longitude,
            location.altitude,
            location.accuracy
        )
    }
}