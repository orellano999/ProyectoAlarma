package com.example.proyectoalarma.sampledata

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectoalarma.databinding.ActivityLlamadaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class LLamada : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var binding: ActivityLlamadaBinding
    private lateinit var database: DatabaseReference
    var contador = 0
    private val job = CoroutineScope(Dispatchers.Main)

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDateTime = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLlamadaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realizarLlamada()

        if (checkInternetStatus()){
            setUpView()
        }
        // Añade el callback para manejar el botón de retroceso
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Tu lógica aquí
                job.cancel()
                // Si quieres permitir el comportamiento por defecto después de tu lógica
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun realizarLlamada() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) ==
            PackageManager.PERMISSION_GRANTED){
            val miIntent = Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:113")
            )
            startActivity(miIntent)
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                5)
            finish()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpView() {
        // Inicializa Firebase
        database = FirebaseDatabase.getInstance().reference
        setUpActions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpActions() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startFlickering()
    }
    private fun startFlickering() {
        job.launch {
            while (true) {
                uwu()
                contador++;
                delay(3000) // Pausa por 500 milisegundos
            }
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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return
        }
        if (checkGPSStatus()){
            //Si se tienen los permisos
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Usa la ubicación aquí
                        val latitude = it.latitude
                        val longitude = it.longitude
                        val data = "Latitud: "+latitude.toString()+"   Longitud :"+longitude.toString()
                        // Muestra la ubicación en un TextView o usa como necesites
                        database.child("ubicacion/mensaje"+contador).setValue(data)
                    }
                }
        }
    }
    private fun checkInternetStatus(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = (connectivityManager.activeNetwork)
        // val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        if (network == null) {
            Toast.makeText(this,
                "Conecta El Internet.",
                Toast.LENGTH_SHORT).show()
            finish()
        } else {

        }
        return true
    }
    fun checkGPSStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGPSEnabled) {
            Toast.makeText(this,
                "Conecta El gps.",
                Toast.LENGTH_SHORT).show()
            //finish()
            return false
        }
        return true
    }

}