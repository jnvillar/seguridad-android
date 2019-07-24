package com.fcen.seguridad.Camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView
import java.io.File
import com.fcen.seguridad.R
import kotlinx.android.synthetic.main.activity_camera.*
import java.text.SimpleDateFormat
import java.util.*
import android.os.AsyncTask

class CameraActivity : AppCompatActivity() {

    var fotoapparat: Fotoapparat? = null
    var fotoapparatState: FotoapparatState? = null
    var cameraStatus: CameraState? = null
    var flashState: FlashState? = null

    val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        createFotoapparat()
        cameraStatus = CameraState.BACK
        flashState = FlashState.OFF
        fotoapparatState = FotoapparatState.OFF

        fab_camera.setOnClickListener {
            takePhoto()
        }
        fab_switch_camera.setOnClickListener {
            switchCamera()
        }
        fab_flash.setOnClickListener {
            changeFlashState()
        }

        AsyncTask.execute {
            switchCamera()
            SystemClock.sleep(1000)
            var iterations = 0
            while (iterations < 10) {
                iterations += 1
                SystemClock.sleep(5000)
                takePhoto()
            }
        }
    }

    private fun createFotoapparat() {
        val cameraView = findViewById<CameraView>(R.id.camera_view)
        fotoapparat = Fotoapparat(
            context = this,
            view = cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = back(),
            logger = loggers(
                logcat()
            ),
            cameraErrorCallback = { error ->
                println("Recorder errors: $error")
            }
        )
    }

    private fun changeFlashState() {
        fotoapparat?.updateConfiguration(
            CameraConfiguration(
                flashMode = if (flashState == FlashState.TORCH) off() else torch()
            )
        )
        if (flashState == FlashState.TORCH) flashState = FlashState.OFF
        else flashState = FlashState.TORCH
    }

    private fun switchCamera() {
        fotoapparat?.switchTo(
            lensPosition = if (cameraStatus == CameraState.BACK) front() else back(),
            cameraConfiguration = CameraConfiguration()
        )
        if (cameraStatus == CameraState.BACK) cameraStatus = CameraState.FRONT
        else cameraStatus = CameraState.BACK
    }

    private fun takePhoto() {
        val timeStampFormat = SimpleDateFormat("yyyyMMddHHmmssSS")
        val myDate = Date()
        val date = timeStampFormat.format(myDate)
        val directoryName = "Pictures/Test/"

        val folder = File((Environment.getExternalStorageDirectory()).toString() + File.separator + directoryName)
        if (!folder.exists()) { folder.mkdirs() }

        val filename = directoryName + date + ".jpg"
        val sd = Environment.getExternalStorageDirectory()
        val dest = File(sd, filename)

        if (hasNoPermissions()) {
            requestPermission()
        } else {
            fotoapparat
                ?.takePicture()
                ?.saveToFile(dest)
        }
    }

    override fun onStart() {
        super.onStart()
        if (hasNoPermissions()) {
            requestPermission()
        } else {
            fotoapparat?.start()
            fotoapparatState = FotoapparatState.ON
        }
    }

    private fun hasNoPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
        FotoapparatState.OFF
    }



    override fun onResume() {
        super.onResume()
        if (!hasNoPermissions() && fotoapparatState == FotoapparatState.OFF) {
            val intent = Intent(baseContext, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}

enum class CameraState {
    FRONT, BACK
}

enum class FlashState {
    TORCH, OFF
}

enum class FotoapparatState {
    ON, OFF
}