package com.sherbek_mamasodiqov.mytaxi.data
import kotlinx.coroutines.flow.Flow

class LocationRepository(private val locationDao: LocationDao) {
    suspend fun insertLocation(latitude: Double, longitude: Double,timestamp: Long) {
        val locationEntity = LocationEntity(latitude = latitude, longitude = longitude, timestamp = timestamp)
        locationDao.insertLocation(locationEntity)
    }

    fun getLatestLocation(): Flow<LocationEntity?> {
        return locationDao.getLatestLocation()
    }

    fun getAllLocations(): Flow<List<LocationEntity>> {
        return locationDao.getAllLocations()
    }

    suspend fun deleteOldLocations(timestamp: Long) {
        locationDao.deleteOldLocations(timestamp)
    }
}