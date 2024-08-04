package com.sherbek_mamasodiqov.mytaxi
import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.location.Location
import android.util.Log
import kotlinx.coroutines.flow.asStateFlow

object LocationManager {
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> get() = _location

    fun updateLocation(newLocation: Location) {
        _location.value = newLocation
        Log.d("TTTT","${newLocation.latitude},${newLocation.longitude}")
    }
}