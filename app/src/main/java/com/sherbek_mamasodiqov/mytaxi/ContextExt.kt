package com.sherbek_mamasodiqov.mytaxi

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasLocationPermission() : Boolean{
    return ContextCompat.checkSelfPermission(
        this,
        "android.permission.ACCESS_COARSE_LOCATION"
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                "android.permission.ACCESS_FINE_LOCATION"
            ) == PackageManager.PERMISSION_GRANTED
}