package com.example.proyectoalarma.sampledata

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoalarma.R
import com.example.proyectoalarma.databinding.ActivityGrabadoraBinding
import com.example.proyectoalarma.databinding.ActivityLlamadaBinding
import com.example.proyectoalarma.sampledata.CameraActivity.Companion.REQUEST_CODE_PERMISSIONS
import com.example.proyectoalarma.sampledata.CameraActivity.Companion.REQUIRED_PERMISSIONS
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class Grabadora : AppCompatActivity() {
    lateinit var mr: MediaRecorder
    lateinit var path: String

    lateinit var binding: ActivityGrabadoraBinding
    private lateinit var database: DatabaseReference
    var contador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrabadoraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        path = getExternalFilesDir(null)?.absolutePath + "/myrec.mp3"
        mr = MediaRecorder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (checkInternetStatus()) {
                setUpView()
            }

            if (allPermissionsGranted()) {
                binding.btPlay.isEnabled = true
                binding.btStart.isEnabled = true
                actividad()
            } else {
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }
        // Añade el callback para manejar el botón de retroceso
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Tu lógica aquí

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
    }

    private fun checkInternetStatus(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = (connectivityManager.activeNetwork)

        if (network == null) {
            Toast.makeText(
                this,
                "Sin Conexion a interner",
                Toast.LENGTH_SHORT
            ).show()
        }
        return true
    }
    fun actividad() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val streamType = AudioManager.STREAM_MUSIC
        var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        if (currentVolume < 5){
            currentVolume = 10
        }

        audioManager.setStreamVolume(streamType, currentVolume, 0)

        // Start Recording
        binding.btStart.setOnClickListener {
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mr.setOutputFile(path)
            mr.prepare()
            mr.start()
            binding.btStop.isEnabled = true
            binding.btStart.isEnabled = false
            binding.btPlay.isEnabled = false
        }

        // Stop Recording
        binding.btStop.setOnClickListener {
            mr.stop()
            mr.reset()
            binding.btStart.isEnabled = true
            binding.btStop.isEnabled = false
            binding.btPlay.isEnabled = true

            if(checkInternetStatus()){//Enviar Al Storage
                val storageRef = FirebaseStorage.getInstance().reference.child("audio/AudioNr"+contador+".mp3")
                val file = Uri.fromFile(File(path))
                storageRef.putFile(file)
            }
            contador++
        }

        // Play Recording
        binding.btPlay.setOnClickListener {
            val mp = MediaPlayer()
            mp.setDataSource(path)
            mp.prepare()
            mp.start()
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                binding.btPlay.isEnabled = true
                binding.btStart.isEnabled = true
                actividad()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
