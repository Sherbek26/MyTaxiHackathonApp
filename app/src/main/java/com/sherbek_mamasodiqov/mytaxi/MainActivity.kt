package com.sherbek_mamasodiqov.mytaxi

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.app.ActivityCompat
import com.sherbek_mamasodiqov.mytaxi.ui.theme.MyTaxiTheme


@OptIn(ExperimentalFoundationApi::class)
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
                MapScreen()
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app needs location permissions to provide location-based services. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                // Request permissions again
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        "android.permission.ACCESS_FINE_LOCATION",
                        "android.permission.ACCESS_COARSE_LOCATION"
                    ),
                    0
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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
