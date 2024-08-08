package com.sherbek_mamasodiqov.mytaxi

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.CameraOptions
import com.sherbek_mamasodiqov.mytaxi.data.AppDatabase
import com.sherbek_mamasodiqov.mytaxi.data.LocationRepository
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@SuppressLint("ReturnFromAwaitPointerEventScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = LocationRepository(database.locationDao())
    val mapViewModel: MapViewModel = viewModel { MapViewModel(repository) }

    val userLocation by mapViewModel.userLocation.collectAsStateWithLifecycle()
    val cameraState by mapViewModel.cameraState.collectAsStateWithLifecycle()

    val mapView = remember {
        com.mapbox.maps.MapView(context).apply {}
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    var isVisible by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.type == PointerEventType.Press) {
                                    event.changes.forEach {
                                        scope.launch {
                                            if (sheetState.currentValue == SheetValue.PartiallyExpanded) {
                                                sheetState.expand()
                                                isVisible = false
                                            } else {
                                                sheetState.partialExpand()
                                                isVisible = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
            )
        },
        sheetPeekHeight = 120.dp,
        sheetContainerColor = if (isSystemInDarkTheme()) colorResource(id = R.color.sheetScaffoldDarkColor) else Color.White,
        sheetDragHandle = null,
        sheetSwipeEnabled = false
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
        {
            MapView(
                userLocation = userLocation,
                cameraState = cameraState,
                mapView = mapView,
                context = context
            )
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart)
            ) {
                MapControlsButton(
                    onClick = {
                        isVisible = true
                        scope.launch {
                            sheetState.partialExpand()
                        }
                    },
                    icon = R.drawable.chevrons,
                    isChevron = true
                )
            }
            AnimatedVisibility(
                visible = isVisible,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    verticalArrangement = (Arrangement.spacedBy(16.dp))
                ) {
                    MapControlsButton(
                        onClick = {
                            mapViewModel.zoomIn()
                            scope.launch {
                                sheetState.partialExpand()
                            }
                        },
                        icon = R.drawable.plus
                    )
                    MapControlsButton(
                        onClick = {
                            mapViewModel.zoomOut()
                            scope.launch {
                                sheetState.partialExpand()
                            }
                        },
                        icon = R.drawable.minus
                    )
                    MapControlsButton(
                        onClick = {
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
                            scope.launch {
                                sheetState.partialExpand()
                            }
                        },
                        icon = R.drawable.navigation,
                        isHasNavigationIcon = true
                    )
                }
            }
            CustomAppTopBar(modifier = Modifier
                .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun MapControlsButton(
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
        @DrawableRes icon: Int,
        isChevron : Boolean = false,
        isHasNavigationIcon : Boolean = false
    ) {
    OutlinedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,

        colors = ButtonDefaults.buttonColors(
            containerColor = if (isChevron) colorResource(id = R.color.chevron_background_color) else if (isSystemInDarkTheme()) Color.Black else Color.White
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        modifier = modifier.size(56.dp),
        contentPadding = PaddingValues(8.dp),
        border = if (isChevron) BorderStroke(
            width = 4.dp,
            color = if (isSystemInDarkTheme()) colorResource(id = R.color.sheetScaffoldDarkColor) else Color.White
        ) else null
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(24.dp)
                .fillMaxSize(),
            colorFilter = if (isHasNavigationIcon) null else ColorFilter.tint(
                color = if(!isChevron) if (isSystemInDarkTheme()) colorResource(id = R.color.item_and_text_color_dark) else colorResource(
                    id = R.color.items_and_text_color_light
                ) else Color.White
            )
        )
    }
}