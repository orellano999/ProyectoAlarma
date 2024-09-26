package com.example.proyectoalarma

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectoalarma.databinding.ActivityMenuBinding
import com.example.proyectoalarma.sampledata.*


class MenuActivity : AppCompatActivity() {

    lateinit var binding: ActivityMenuBinding
    val luz = FlashLight()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val btnCamera : Button = findViewById(R.id.CameraFrontal)
        val btnSOS : Button = findViewById(R.id.SOS)
        val btnLLamar : Button = findViewById(R.id.LlamarId)
        val btnMapa : Button = findViewById(R.id.Mapa)
        val btnMicrofono : Button = findViewById(R.id.Microfono)


        btnMicrofono.setOnClickListener{
            navigateTobtnMicrofono()
        }
        btnCamera.setOnClickListener{
            navigateToCamera()
        }
        binding.Linterna.setOnClickListener{
            setupUi(luz)
        }
        btnSOS.setOnClickListener{
            navigateToSOS()
        }
        btnLLamar.setOnClickListener{
            navigateToLlamada()
         }
        btnMapa.setOnClickListener{
            navigateToMapa()
        }
        binding.Chat.setOnClickListener{
            navigateToChat()
        }
        // Añade el callback para manejar el botón de retroceso
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Lógica aquí
                luz.disableFlash(this@MenuActivity)
                // Si queres permitir el comportamiento por defecto
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        /*
        btnCameraBack.setOnClickListener{
            navigateToCameraBack()
        }*/

    }

    private fun navigateToLlamada() {
        val intent = Intent(this, LLamada::class.java)
        startActivity(intent)
    }

    private fun navigateToChat() {
        val intent = Intent(this, Chat::class.java)
        startActivity(intent)
    }

    private fun navigateToMapa() {
        val intent = Intent(this, Mapa::class.java)
        startActivity(intent)
    }
    private fun navigateTobtnMicrofono() {
        val intent = Intent(this, Grabadora::class.java)
        startActivity(intent)
    }
    private fun setupUi(luz: FlashLight) {
        luz.toggleFlash(this,binding)
    }

    private fun navigateToCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSOS() {
        val intent = Intent(this, SosActivity::class.java)
        startActivity(intent)
    }
}