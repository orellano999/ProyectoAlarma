package com.example.proyectoalarma.sampledata

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectoalarma.databinding.ActivityChatBinding
import com.example.proyectoalarma.sampledata.CameraActivity.Companion.REQUEST_CODE_PERMISSIONS
import com.example.proyectoalarma.sampledata.CameraActivity.Companion.REQUIRED_PERMISSIONS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class Chat : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var cont = 0;
    var contUsuario = 0;

    var contador = 0
    private val job = CoroutineScope(Dispatchers.Main)

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDateTime = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkInternetStatus()){
            setUpView()
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            setUpActions()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpView() {
        // Inicializa Firebase
        database = FirebaseDatabase.getInstance().reference
        setUpActions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpActions() {
        binding.uploadButtonId.setOnClickListener {
            uploadData()
        }
        binding.EnviarUbicacion.setOnClickListener{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            startFlickering()
        }
    }


    // Escribe datos en la base de datos
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadData() {

        checkInternetStatus()
        //Recupera Hora
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)

        val data = binding.Conversar.text.toString()
        database.child("ruta/mensaje"+contUsuario).setValue(data)
        agregarMsj(data,formattedDateTime)
        downloadData()

    }
    fun agregarMsj(data: String, formattedDateTime: String) {
        val newTextViewFecha = TextView(this@Chat).apply {
            text = formattedDateTime
            textSize = 15f
            setPadding(16, 0, 0, 25)
            setTextColor(Color.BLACK)
        }
        val newTextView = TextView(this@Chat).apply {
            text = data
            textSize = 15f
            setPadding(16, 16, 16, 0)
            setTextColor(Color.BLUE)
        }
        contUsuario++
        binding.layout.addView(newTextView)
        binding.layout.addView(newTextViewFecha)
    }
    // Lee datos de la base de datos
    @RequiresApi(Build.VERSION_CODES.O)
    fun downloadData() {
        //Recupera Hora
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)

        database.child("policia/respuesta"+cont).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var dato = snapshot.getValue(String::class.java)
                // Manejar el dato leído
                //Toast.makeText(this@Chat, dato, Toast.LENGTH_SHORT).show()
                if (dato == null){
                    dato = "Sin Connexion"
                }
                val newTextView = TextView(this@Chat).apply {
                    text = dato
                    textSize = 15f
                    setPadding(16, 8, 8, 8)
                    setTextColor(Color.RED)
                }
                val newTextViewFecha = TextView(this@Chat).apply {
                    text = formattedDateTime
                    textSize = 15f
                    setPadding(16, 0, 0, 25)
                    setTextColor(Color.BLACK)
                }
                binding.layout.addView(newTextView)
                binding.layout.addView(newTextViewFecha)
                cont++
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de lectura
                print(error.message)
            }
        })
    }
    // Actualiza datos en la base de datos
    fun updateData(newData: String) {
        val actualizacion = HashMap<String, Any>()
        actualizacion["data"] = newData
        database.child("ruta").updateChildren(actualizacion)
    }
    private fun startFlickering() {
        job.launch {
            while (true) {
                uwu(false)
                contador++;
                delay(3000) // Pausa por 500 milisegundos
            }
        }
    }


    fun uwu(abrirApp : Boolean){
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

            binding.EnviarUbicacion.setBackgroundColor(Color.GREEN)
            binding.EnviarUbicacion.text = "Enviando Ubicacion"
            binding.textView.visibility = View.VISIBLE

            //Si se tienen los permisos
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Usa la ubicación aquí
                        val latitude = it.latitude
                        val longitude = it.longitude
                        val data = "Latitud: "+latitude.toString()+"   Longitud :"+longitude.toString()

                        binding.textView.text = data
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
            return false
        }
        return true
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                uwu(false)
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}


