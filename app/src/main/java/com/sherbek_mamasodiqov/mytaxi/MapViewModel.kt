package com.sherbek_mamasodiqov.mytaxi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _userLocation = MutableStateFlow<Point?>(null)
    val userLocation: StateFlow<Point?> get() = _userLocation

    private val _followUser = MutableStateFlow(true)
    val followUser: StateFlow<Boolean> get() = _followUser

    private val _cameraState = MutableStateFlow(CameraOptions.Builder().zoom(14.0).build())
    val cameraState: StateFlow<CameraOptions> get() = _cameraState

    fun setUserLocation(location: Point) {
        _userLocation.value = location
        if (_followUser.value) {
            updateCamera(location)
        }
    }

    fun setFollowUser(follow: Boolean) {
        _followUser.value = follow
        if (_followUser.value) {
            _userLocation.value?.let { updateCamera(it) }
        }
    }

    fun zoomIn() {
        _cameraState.update { currentState ->
            val newZoom = (currentState.zoom ?: 14.0) + 1.0
            CameraOptions.Builder()
                .center(currentState.center)
                .zoom(newZoom.coerceAtMost(22.0))
                .bearing(currentState.bearing)
                .pitch(currentState.pitch)
                .build()
        }
    }

    fun zoomOut() {
        _cameraState.update { currentState ->
            val newZoom = (currentState.zoom ?: 14.0) - 1.0
            CameraOptions.Builder()
                .center(currentState.center)
                .zoom(newZoom.coerceAtLeast(0.0))
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