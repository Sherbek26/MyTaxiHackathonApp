package com.sherbek_mamasodiqov.mytaxi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sherbek_mamasodiqov.mytaxi.ui.theme.MyTaxiTheme

/**
 * Tracks the user location on screen, simulates a navigation session.
 */
class MainActivity : ComponentActivity() {

    private val locationServiceIntent by lazy {
        Intent(this, LocationService::class.java)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContent {
            MyTaxiTheme {
                MapView()
                startLocationService()
            }
        }
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION"
            ),
            0
        )
    }
    private fun startLocationService() {
        locationServiceIntent.action = LocationService.ACTION_START
        startService(locationServiceIntent)
    }

    private fun stopLocationService() {
        locationServiceIntent.action = LocationService.ACTION_STOP
        startService(locationServiceIntent)
    }
    override fun onDestroy() {
        super.onDestroy()
        // Stop the LocationService when the activity is destroyed
        stopLocationService()
    }

}