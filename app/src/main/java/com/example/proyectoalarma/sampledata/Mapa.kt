package com.example.proyectoalarma.sampledata


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.proyectoalarma.databinding.ActivityMapaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


// Implement OnMapReadyCallback.
class Mapa : AppCompatActivity(){
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var binding: ActivityMapaBinding
    var estado = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            binding = ActivityMapaBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btUbicacion.setOnClickListener{
                start()
            }
            start()
        }
    fun start(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkGPSStatus()
        uwu()
    }

    fun checkGPSStatus() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGPSEnabled) {
            Toast.makeText(this,
                "Conecta El gps.",
                Toast.LENGTH_SHORT).show()
            finish()
        } else {

        }
    }

    fun uwu(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            Toast.makeText(this,
                "Permissions not granted by the user.",
                Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        //Si se tienen los permisos
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Usa la ubicación aquí
                        val latitude = it.latitude
                        val longitude = it.longitude
                        // Muestra la ubicación en un TextView o usa como necesites
                        binding.tvLongitud.text="Longitud :"+longitude.toString()
                        binding.tvLatitud.text="Latitude: "+latitude.toString()

                        maps(longitude,latitude)

                    } /*?: run{
                        Toast.makeText(this,
                            "Permissions not granted by the user.",
                            Toast.LENGTH_SHORT).show()
                    }*/
                }
        }
    fun maps(longitude: Double, latitude: Double) {
        val miIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:$longitude.toString(),$latitude.toString()")
        )
        startActivity(miIntent)
    }

}