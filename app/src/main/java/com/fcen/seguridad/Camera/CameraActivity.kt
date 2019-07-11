package com.fcen.seguridad.Camera

import android.content.pm.PackageManager
import android.os.Bundle
import com.androidhiddencamera.HiddenCameraActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.androidhiddencamera.CameraError
import com.androidhiddencamera.HiddenCameraUtils
import com.androidhiddencamera.config.CameraRotation
import com.androidhiddencamera.config.CameraImageFormat
import com.androidhiddencamera.config.CameraResolution
import com.androidhiddencamera.config.CameraFacing
import com.androidhiddencamera.CameraConfig

class CameraActivity : HiddenCameraActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mCameraConfig = CameraConfig()
            .getBuilder(this)
            .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
            .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
            .setImageRotation(CameraRotation.ROTATION_270)
            .build()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //Start camera preview
            startCamera(mCameraConfig);
        }
    }

    override fun onCameraError(@CameraError.CameraErrorCodes errorCode: Int) {
        when (errorCode) {
            CameraError.ERROR_CAMERA_OPEN_FAILED -> {
            }
            CameraError.ERROR_IMAGE_WRITE_FAILED -> {
            }
            CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE -> {
            }
            CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION ->
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this)
            CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA -> Toast.makeText(
                this,
                "Your device does not have front camera.",
                Toast.LENGTH_LONG
            ).show()
        }//Camera open failed. Probably because another application
        //is using the camera
        //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
        //camera permission is not available
        //Ask for the camra permission before initializing it.
    }
}

