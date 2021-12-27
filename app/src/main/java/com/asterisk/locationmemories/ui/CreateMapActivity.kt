package com.asterisk.locationmemories.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.asterisk.locationmemories.R
import com.asterisk.locationmemories.databinding.ActivityCreateMapBinding
import com.asterisk.locationmemories.databinding.DialogCreatePlaceBinding
import com.asterisk.locationmemories.other.Constants.EXTRA_MAP_TITLE

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapBinding
    private var markers: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = intent.getStringExtra(EXTRA_MAP_TITLE)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.let {
            Snackbar.make(it, "Long press to add a marker", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok") {}
                .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                .show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener { markerToDelete ->
            markers.remove(markerToDelete)
            markerToDelete.remove()
        }

        mMap.setOnMapLongClickListener { latLng ->
            showAlertDialog(latLng)
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        AlertDialog.Builder(this)
            .setTitle("Create a marker")
            .setView(placeFormView)
            .setNegativeButton("cancel", null)
            .setPositiveButton("yes") { dialogInterface, _ ->
                val title = placeFormView.findViewById<EditText>(R.id.et_title).text.toString()
                val desc = placeFormView.findViewById<EditText>(R.id.et_description).text.toString()

                if (title.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Place must not have empty title and description",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val mark = mMap.addMarker(
                    MarkerOptions().position(latLng).title(title)
                        .snippet(desc)
                )

                if (mark != null) {
                    markers.add(mark)
                }
                dialogInterface.dismiss()
            }
            .show()


    }
}