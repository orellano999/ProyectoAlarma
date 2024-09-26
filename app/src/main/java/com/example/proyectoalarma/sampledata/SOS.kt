package com.example.proyectoalarma

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class SosActivity : AppCompatActivity() {
    private lateinit var miLayout: LinearLayout
    private val job = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sos)
        miLayout = findViewById(R.id.mi_layout)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val streamType = AudioManager.STREAM_MUSIC
        var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        if (currentVolume<5){
            currentVolume = 10
        }
        audioManager.setStreamVolume(streamType, currentVolume, 0)


        //Creacion Sonido
        var mediaPlayer = MediaPlayer.create(this, R.raw.nivelsoundtrack)
        mediaPlayer.start()


        // Añade el callback para manejar el botón de retroceso
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Tu lógica aquí
                mediaPlayer.stop()
                job.cancel()
                // Si quieres permitir el comportamiento por defecto después de tu lógica
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        // Iniciar el ciclo de titileo
        startFlickering()

    }
        private fun startFlickering() {
            job.launch {
                while (true) {
                    miLayout.setBackgroundColor(Color.RED)
                    delay(500L) // Pausa por 500 milisegundos
                    miLayout.setBackgroundColor(Color.WHITE)
                    delay(500L) // Pausa por 500 milisegundos
                }
            }
        }
    }

