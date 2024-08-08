package com.sherbek_mamasodiqov.mytaxi

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.locationcomponent.location

@SuppressLint("Lifecycle", "IncorrectNumberOfArgumentsInExpression")
@Composable
fun MapView(
    userLocation : Point?,
    cameraState : CameraOptions,
    mapView: MapView,
    context:Context,
    modifier: Modifier = Modifier
) {

    DisposableEffect(context) {
        mapView.onStart()
        onDispose {
            mapView.onDestroy()
        }
    }

    val isDarkMode = isSystemInDarkTheme()

    LaunchedEffect(userLocation,cameraState,isDarkMode) {
        mapView.getMapboxMap().apply {
            val mapStyle = if (isDarkMode) {
                "mapbox://styles/sherbek2615/clzksgesk000a01pd5kofd0v9"// Dark style with streets
            } else {
                "mapbox://styles/sherbek2615/clzkt6cx5000d01pd93bibf76" // Light style with streets
            }
            loadStyle(mapStyle) { style ->
                // Update camera position
                setCamera(cameraState)
                // Set up LocationComponent
                mapView.location.updateSettings {
                    enabled = true
                    locationPuck = LocationPuck2D(
                        bearingImage = ImageHolder.from(R.drawable.new_car_pin),
                        scaleExpression = interpolate {
                            linear()
                            zoom()
                            stop {
                                literal(0.0)
                                literal(0.6)
                            }
                            stop {
                                literal(20.0)
                                literal(1.0)
                            }
                        }.toJson()
                    )
                }
                // Enable location component
                mapView.location.apply {
                    updateSettings {
                        enabled = true
                    }
                }
            }
        }
    }
    // Layout for buttons and map
        AndroidView(
            factory = { mapView },
            modifier = modifier.fillMaxSize()
        )
}
