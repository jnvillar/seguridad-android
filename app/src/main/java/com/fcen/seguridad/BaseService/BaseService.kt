package com.fcen.seguridad.BaseService

import android.content.Context
import android.location.Location
import android.os.SystemClock
import android.util.Log
import androidx.work.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.io.File

class BaseService(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun doWork(): Result {
        val timeStampFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")
        var iterations = 0
        while (iterations < 180) {
            val myDate = Date()
            val date = timeStampFormat.format(myDate)
            Log.d("SERVICE", "[${date}]: service running")
            iterations += 1
            logLocation(date)
            doRequest()
            SystemClock.sleep(5000)
        }
        return Result.success()
    }

    private fun write(data:String) {
        val path = this.applicationContext.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val mFile = File(letDirectory, "locations.txt")
        if (!mFile.exists()){
            mFile.appendText(data)
            return
        }
        val lines = mFile.readLines()
        val linesToWrite = arrayOf(data) + lines
        mFile.delete()

        val file = File(letDirectory, "locations.txt")
        linesToWrite.forEach { line ->
            file.appendText(line)
        }
    }

    fun logLocation(date: String) {
        val permissionGranted = ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val locationPermissionGranted = ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionGranted == PackageManager.PERMISSION_GRANTED && locationPermissionGranted == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.applicationContext)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lastLocation = LatLng(location.latitude, location.longitude)
                    write("[${date}]: lat:${lastLocation.latitude} lng:${lastLocation.longitude} \n")
                    Log.d("SERVICE", "[${date}]: last location ${lastLocation}.")
                }
            }
        }
    }

    fun doRequest() {
        val queue = Volley.newRequestQueue(this.applicationContext)
        val url = "https://google.com"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Log.d("SERVICE", "Response is: ${response.substring(0, 500)}")
            },
            Response.ErrorListener { error -> Log.d("SERVICE", "fallo ${error}") })
        queue.add(stringRequest)
    }

    companion object {
        fun scheduleWorker(): UUID { //UUID is the ID of the worker
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val worker = PeriodicWorkRequestBuilder<BaseService>(1, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance().enqueueUniquePeriodicWork("worker", ExistingPeriodicWorkPolicy.REPLACE, worker)
            return worker.id
        }
    }
}