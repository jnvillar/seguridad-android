package com.fcen.seguridad

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Intent
import com.fcen.seguridad.BaseService.BaseService
import com.fcen.seguridad.Camera.CameraActivity
import com.fcen.seguridad.Location.LocationActivity
import com.fcen.seguridad.Sms.SmsActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        Log.d("NAVIGATION", "antes del service")
        BaseService.scheduleWorker()
        Log.d("NAVIGATION", "despues del service")
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        Log.d("NAVIGATION", "click on navigation")
        when (item.itemId) {
            R.id.nav_sms -> {
                val hasPermissions = checkPermissions(android.Manifest.permission.READ_SMS)
                Log.d("SMS", "searching sms")
                if (hasPermissions == false) {
                    return true
                }
                intent = Intent(this, SmsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_camera -> {
                Log.d("NAVIGATION", "camera")
                val hasCameraP = checkPermissions(android.Manifest.permission.CAMERA)
                if (hasCameraP == false) {
                    return true
                }
                val hasWriteP = checkPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (hasWriteP == false) {
                    return true
                }
                val hasReadP = checkPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                if (hasReadP == false) {
                    return true
                }
                Log.d("NAVIGATION", "creating camera activity")
                intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_location -> {
                Log.d("NAVIGATION", "click on location")
                val hasAccessFineLocationPermissions = checkPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
                if (hasAccessFineLocationPermissions == false) {
                    return true
                }
                val hasAccessCoarseLocationPermissions = checkPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                if (hasAccessCoarseLocationPermissions == false) {
                    return true
                }
                Log.d("NAVIGATION", "creating location activity")
                intent = Intent(this, LocationActivity::class.java)
                startActivity(intent)
            }
        }
        Log.d("NAVIGATION", "navbar action finished")
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkPermissions(permission :String): Boolean {
        val permissionGranted = ContextCompat.checkSelfPermission(this, permission)
        if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
            return false
        }
        return true
    }

}


