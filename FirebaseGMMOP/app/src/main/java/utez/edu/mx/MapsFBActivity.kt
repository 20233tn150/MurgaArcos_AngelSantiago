package utez.edu.mx

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import utez.edu.mx.adapters.MarkerAdapter
import utez.edu.mx.adapters.MarkerItem
import utez.edu.mx.databinding.ActivityMapsFbactivityBinding

class MapsFBActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var LOCATION_PERMISSION_REQUEST = 1
    private var userLocation: LatLng? = null
    private var markerItems = mutableListOf<MarkerItem>()
    private lateinit var markersRef: DatabaseReference
    private lateinit var binding: ActivityMapsFbactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsFbactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFirebaseReference()
        setupMapFragment()
        setupButtons()
        setupRecyclerView()

    }




    // Configura la referencia a la base de datos Firebase
    private fun setupFirebaseReference() {
        val database = FirebaseDatabase.getInstance()
        markersRef = database.getReference("markers")
    }

    // Configura el fragmento del mapa
    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Configuración de los botones
    private fun setupButtons() {
        findViewById<Button>(R.id.btnCenterLocation).setOnClickListener {
            userLocation?.let {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            } ?: Toast.makeText(this, "Ubicación no disponoble", Toast.LENGTH_SHORT).show()
        }
    }

    // Configura el RecyclerView para mostrar los marcadores
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = MarkerAdapter(markerItems)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        markersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                markerItems.clear()
                for (markerSnapshot in snapshot.children){
                    val latitude = markerSnapshot.child("latitude").value as Double
                    val longitude = markerSnapshot.child("longitude").value as Double
                    val title = markerSnapshot.child("title").value as String
                    val markerPosition = LatLng(latitude, longitude)

                    markerItems.add(MarkerItem(title, "Lat: ${latitude}, Lng: ${longitude}"))

                    map.addMarker(
                        MarkerOptions().position(markerPosition).title("$title (Lat: $latitude Lng: $longitude)")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapsFBActivity, "Error al cargar marcadores: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Se llama cuadno el mapa está listo para usarse
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        setupMapListeners()
        enableLocationIfPermitted()
    }


    private fun setupMapListeners() {
        map.setOnMapClickListener { latLng ->
            val markerTitle = "Punto de interés (Lat: ${latLng.latitude}, Lng: ${latLng.longitude})"
            val markerData = mapOf(
                "latitude" to latLng.latitude,
                "longitude" to latLng.longitude,
                "title" to markerTitle
            )
            markersRef.push().setValue(markerData)

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(markerTitle)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            markerItems.add(MarkerItem("Punto de interés",
                "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"))
        }
    }

    // Habilita la ubicación si los permisos están concedidos
    private fun enableLocationIfPermitted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }
        map.isMyLocationEnabled = true
        map.setOnMyLocationChangeListener { location: Location ->
            userLocation = LatLng(location.latitude, location.longitude)
        }

    }

    /**
     * Maneja el resultado de la solicitud de permisos
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationIfPermitted()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}