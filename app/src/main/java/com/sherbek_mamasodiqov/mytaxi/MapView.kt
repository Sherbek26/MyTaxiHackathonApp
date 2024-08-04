package com.sherbek_mamasodiqov.mytaxi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CameraState
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.locationcomponent.location

@SuppressLint("Lifecycle", "IncorrectNumberOfArgumentsInExpression")
@Composable
fun MapView(mapViewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val userLocation by mapViewModel.userLocation.collectAsStateWithLifecycle()
    val cameraState by mapViewModel.cameraState.collectAsStateWithLifecycle()
    val followUser by mapViewModel.followUser.collectAsState()
    val mapView = remember {
       MapView(context).apply {}
    }
    DisposableEffect(context) {
        mapView.onStart()
        onDispose {
            mapView.onDestroy()
        }
    }

    LaunchedEffect(userLocation,cameraState,followUser) {
        mapView.getMapboxMap().apply {
            loadStyle(Style.MAPBOX_STREETS) { style ->
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
                        pulsingEnabled = true
                    }
                    addOnIndicatorPositionChangedListener { point ->
                        mapViewModel.setUserLocation(point)
                    }
                }
            }
        }
    }
    // Layout for buttons and map
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        TransparentButton(
            onClick = { },
            icon = R.drawable.chevrons,
            modifier = Modifier.padding(16.dp).align(Alignment.CenterStart)
        )

        // Buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = (Arrangement.spacedBy(16.dp))
        ) {
            TransparentButton(
                onClick = { mapViewModel.zoomIn() },
                icon = R.drawable.plus
            )

            TransparentButton(onClick = { mapViewModel.zoomOut() }, icon = R.drawable.minus)
            TransparentButton(onClick = {
                userLocation?.let { point ->
                    mapView.getMapboxMap().apply {
                        setCamera(
                            CameraOptions.Builder()
                                .center(point)
                                .zoom(14.0)
                                .build()
                        )
                    }
                    mapViewModel.setFollowUser(true)
                }
            },
                icon = R.drawable.navigation
            )
        }
    }
}

@Composable
fun TransparentButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes icon: Int
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color.White, // Background of the button
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        modifier = modifier.size(56.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(24.dp)
                .fillMaxSize()
        )
    }
}
