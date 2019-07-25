package com.fcen.seguridad.LocationLog

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fcen.seguridad.R
import java.io.File
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class LocationLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_log)

        val myDate = Date()
        val timeStampFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")
        val date = timeStampFormat.format(myDate)

        Log.d("LOG_LOCATION", "[${date}]: creating view")

        val path = this.applicationContext.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val file = File(letDirectory, "locations.txt")
        val lines = file.readLines()
        val tv = findViewById<View>(R.id.txtContent) as TextView
        tv.text = lines.toString()
    }
}