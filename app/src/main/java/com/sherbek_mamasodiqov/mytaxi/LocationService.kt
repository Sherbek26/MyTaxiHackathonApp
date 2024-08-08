package com.sherbek_mamasodiqov.mytaxi

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.sherbek_mamasodiqov.mytaxi.data.AppDatabase
import com.sherbek_mamasodiqov.mytaxi.data.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var locationRepository: LocationRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        val database = AppDatabase.getDatabase(applicationContext)
        locationRepository = LocationRepository(database.locationDao())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start() {
        locationClient
            .getLocationUpdates(90000L)  // during 1,5 minute interval, service track and get the user's new location
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                serviceScope.launch {
                    locationRepository.insertLocation(location.latitude,location.longitude, timestamp = location.time)
                    Log.d("TTTT",locationRepository.getLatestLocation().toString())
                }
            }
            .launchIn(serviceScope)
        serviceScope.launch {
            while (true) {
                delay(24 * 60 * 60 * 1000) // periodically clean up old data from the database
                val oneDayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
                locationRepository.deleteOldLocations(oneDayAgo)
            }
        }
    }

    private fun stop() {
        serviceScope.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
