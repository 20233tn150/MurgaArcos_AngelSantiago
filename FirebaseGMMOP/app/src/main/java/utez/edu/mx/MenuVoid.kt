package utez.edu.mx

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuVoid : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1
    private var userLocation: LatLng? = null
    private var placeName: String = "Lugar desconocido"
    private var temperature: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_void)

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val nextButton: MaterialButton = findViewById(R.id.next)

        // Configuración del botón "Registrar"
        nextButton.setOnClickListener {
            if (userLocation != null) {
                // Procesa los datos obtenidos previamente
                if (temperature != 0.0) {
                    redirectToActivity(temperature, placeName)
                } else {
                    fetchWeatherData(userLocation!!.latitude, userLocation!!.longitude)
                }
            } else {
                Toast.makeText(this, "No se ha seleccionado ni centrado una ubicación", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuración del mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        // Habilitar ubicación si se tienen permisos
        enableLocationIfPermitted()

        // Listener para clics en el mapa
        map.setOnMapClickListener { latLng ->
            userLocation = latLng
            placeName = "Lugar desconocido" // Reinicia datos previos
            temperature = 0.0 // Reinicia datos previos

            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
            Toast.makeText(this, "Ubicación seleccionada manualmente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableLocationIfPermitted() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }
        map.isMyLocationEnabled = true
    }

    private fun centerUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f))
                    Toast.makeText(this, "Ubicación centrada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
                Log.e("ERROR", "Error al obtener la ubicación: ${it.message}")
            }
        } else {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(OpenWeatherApi::class.java)
        val call = api.getWeather(lat, lon, "fa73f044635c08fe35445a9e1b7cfef0")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    if (weather != null) {
                        placeName = weather.name ?: "Sin nombre"
                        temperature = weather.main.temp

                        Log.d("DEBUG", "Datos obtenidos: Lugar: $placeName, Temp: $temperature°C")
                        Toast.makeText(this@MenuVoid, "Obtenido: $placeName, $temperature°C", Toast.LENGTH_SHORT).show()
                        redirectToActivity(temperature, placeName)
                    } else {
                        Toast.makeText(this@MenuVoid, "Error: Respuesta nula de la API", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MenuVoid, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MenuVoid, "Error al conectar con la API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun redirectToActivity(temp: Double, placeName: String) {
        val intent = when {
            temp < 15 -> Intent(this, MenuLluviosoActivity::class.java)
            temp in 15.0..25.0 -> Intent(this, MenuModeradoActivity::class.java)
            else -> Intent(this, MenuSoleadoActivity::class.java)
        }

        intent.putExtra("place_name", placeName)
        intent.putExtra("temperature", temp)

        Log.d("DEBUG", "Redirigiendo a actividad con: Lugar: $placeName, Temp: $temp°C")
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationIfPermitted()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
