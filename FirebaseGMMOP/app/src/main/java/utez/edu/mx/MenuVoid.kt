package utez.edu.mx

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton

class MenuVoid : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    private var userLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_void)

        // ------------------------------------------------------------
        val next: MaterialButton = findViewById(R.id.next)

        next.setOnClickListener {
            val intent = Intent(this, MenuModeradoActivity::class.java)
            startActivity(intent)
        }
        // ------------------------------------------------------------

        // Configura el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configura el botón para centrar la ubicación
        val btnMyLocation: MaterialButton = findViewById(R.id.btnMyLocation)
        btnMyLocation.setOnClickListener {
            userLocation?.let {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            } ?: Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    // Se llama cuando el mapa está listo para usarse
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        // Habilitar la ubicación si se tienen los permisos
        enableLocationIfPermitted()
    }

    // Habilita la ubicación si los permisos son concedidos
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
        map.setOnMyLocationChangeListener { location: Location ->
            // Al recibir la ubicación, actualizamos la variable userLocation
            userLocation = LatLng(location.latitude, location.longitude)
        }
    }

    // Maneja la respuesta a la solicitud de permisos
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
