package com.sherbek_mamasodiqov.mytaxi

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

//    Flow is using to handle streams of location data that can change over time
    fun getLocationUpdates(interval: Long) : Flow<Location>

    class LocationException(message : String) : Exception()
}