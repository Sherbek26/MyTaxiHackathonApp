package com.sherbek_mamasodiqov.mytaxi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.sherbek_mamasodiqov.mytaxi.data.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(private val locationRepository: LocationRepository) : ViewModel() {
    private val _userLocation = MutableStateFlow<Point?>(null)
    val userLocation: StateFlow<Point?> get() = _userLocation

    private val _cameraState = MutableStateFlow(CameraOptions.Builder().zoom(14.0).build())
    val cameraState: StateFlow<CameraOptions> get() = _cameraState

    init {
        viewModelScope.launch {
            fetchLatestLocation()
        }
    }

    private fun fetchLatestLocation() {
        viewModelScope.launch {
            locationRepository.getLatestLocation().collect { locationEntity ->
                locationEntity?.let {
                    val point = Point.fromLngLat(it.longitude, it.latitude)
                    _userLocation.value = point
                    updateCamera(point)
                }
            }
        }
    }

    fun setFollowUser(follow: Boolean) {
        if (follow) {
            _userLocation.value?.let { updateCamera(center = it) }
        }
    }

    fun zoomIn() {
        _cameraState.update { currentState ->
            val newZoom = (currentState.zoom ?: 14.0) + 1.0
            CameraOptions.Builder()
                .zoom(newZoom.coerceIn(0.0, 22.0)) // Adjust zoom level
                .bearing(currentState.bearing)
                .pitch(currentState.pitch)
                .build()
        }
    }

    fun zoomOut() {
        _cameraState.update { currentState ->
            val newZoom = (currentState.zoom ?: 14.0) - 1.0
            CameraOptions.Builder()
                .zoom(newZoom.coerceIn(0.0, 22.0)) // Adjust zoom level
                .bearing(currentState.bearing)
                .pitch(currentState.pitch)
                .build()
        }
    }

    private fun updateCamera(center: Point) {
        _cameraState.update { currentState ->
            CameraOptions.Builder()
                .center(center)
                .zoom(currentState.zoom)
                .bearing(currentState.bearing)
                .pitch(currentState.pitch)
                .build()
        }
    }
}
