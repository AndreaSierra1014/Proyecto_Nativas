package com.techstore.app.ui.common

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLocation = LatLng(4.7110, -74.0721)
    private var mapReady = false

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted && mapReady) {
                showCurrentLocation()
            } else if (!granted) {
                Toast.makeText(
                    this,
                    "Sin permiso de ubicación. Puedes seleccionar el punto manualmente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this).apply { id = android.view.View.generateViewId() }
        setContentView(root)
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(root.id, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)

        val confirmButton = Button(this).apply {
            text = getString(com.techstore.app.R.string.confirm_location)
            setOnClickListener {
                setResult(
                    RESULT_OK,
                    Intent()
                        .putExtra(EXTRA_LATITUDE, selectedLocation.latitude)
                        .putExtra(EXTRA_LONGITUDE, selectedLocation.longitude)
                )
                finish()
            }
        }
        root.addView(
            confirmButton,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ).apply {
                bottomMargin = (24 * resources.displayMetrics.density).toInt()
            }
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapReady = true
        map.setOnMapClickListener { location ->
            selectedLocation = location
            map.clear()
            map.addMarker(MarkerOptions().position(location).title("Punto de entrega"))
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 14f))

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> showCurrentLocation()
            else -> requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showCurrentLocation() {
        map.isMyLocationEnabled = true
        LocationServices.getFusedLocationProviderClient(this).lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    selectedLocation = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 16f))
                    map.addMarker(
                        MarkerOptions().position(selectedLocation).title("Punto de entrega")
                    )
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "No se pudo obtener la ubicación: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    companion object {
        const val EXTRA_LATITUDE = "lat"
        const val EXTRA_LONGITUDE = "lon"
    }
}
