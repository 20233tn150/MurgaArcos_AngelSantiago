package utez.edu.mx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import utez.edu.mx.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var mapTypeIndex = 0 // Indice para altermnar entre los tipos de mapa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obteenr el fragmento del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar el botón para cambiar el tipo de mapa
        findViewById<Button>(R.id.btnChangeMapType).setOnClickListener{
            changeMapType()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Habilitar los controles de zoom
        map.uiSettings.isZoomControlsEnabled = true
        map.isTrafficEnabled = true
        map.isBuildingsEnabled = true

        // Configurar una ubicación inicial (Ejem: CDMX)
        val mexicoCity = LatLng(19.432608, -99.133209)
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(mexicoCity, 12f))

        // Agregar un marcador en la ubicaicón inicial
        map.addMarker(MarkerOptions().position(mexicoCity).title("CDMX"))

        // Agregar otro marcador con una descripción personalidad
        val guadalajara = LatLng(20.659698, -103.349609)
        map.addMarker(MarkerOptions().position(guadalajara).title("Guadalajara").snippet("Capital de Jalisco"))


        val casaMurga = LatLng(18.924623728231076, -99.24424141263533)
        map.addMarker(MarkerOptions().position(casaMurga).title("Casa Murga").snippet("Aqui vive el gran Murga"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(casaMurga, 12f))

    }

    private fun changeMapType() {
        // Lista de tipos de mapa
        val mapTypes = listOf(
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_HYBRID
        )

        // Alternar el tipo de mapa
        mapTypeIndex = (mapTypeIndex + 1) % mapTypes.size
        map.mapType = mapTypes[mapTypeIndex]

    }
}