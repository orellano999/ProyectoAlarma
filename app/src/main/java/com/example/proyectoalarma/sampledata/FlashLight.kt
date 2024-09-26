package com.example.proyectoalarma.sampledata
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import com.example.proyectoalarma.databinding.ActivityMenuBinding


class FlashLight() {

    private var flashEnabled = false

    fun toggleFlash(context: Context, linterna: ActivityMenuBinding) {
        if (hasFlash(context)) {//Si soporta el flash pasa
            if (flashEnabled) {//Estado actual del flash
                disableFlash(context)
                linterna.Linterna.setBackgroundColor(Color.RED)
            } else {
                //android:background="@color/design_default_color_background"
                linterna.Linterna.setBackgroundColor(Color.CYAN)
                enableFlash(context)
            }
        }
    }

    fun enableFlash(context: Context) {
        flashEnabled = true
        setTorchMode(context, flashEnabled)
    }

    fun disableFlash(context: Context) {

        flashEnabled = false
        setTorchMode(context, flashEnabled)
    }

    private fun hasFlash(context: Context): Boolean {//Posee flash
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private fun getCameraManager(context: Context): CameraManager {
        return context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }


    private fun setTorchMode(context: Context, mode: Boolean) {
        try {
            val cameraManager = getCameraManager(context)
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, mode)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}