@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.dangerousmapv10.Map

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.drawable.Icon
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dangerousmapv10.Location.LocationService
import com.example.dangerousmapv10.R
import com.example.dangerousmapv10.showPoints
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context):Location?{
    val client=LocationServices.getFusedLocationProviderClient(context)
    client.lastLocation.addOnSuccessListener { location:Location?->
        return@addOnSuccessListener
    }
    return null
}


@SuppressLint("MissingPermission")
@Composable
fun Map(modifier: Modifier, navController: NavController) {
    val client=LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val isOpen = remember { mutableStateOf(false) }
    var uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    var properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            )
        )
    }
    val singapore = LatLng(1.35, 103.87)
    val cairo = LatLng(30.0444, 31.2357)
    val cairoState = MarkerState(position = cairo)
    var currLatLong by remember {
        mutableStateOf(LatLng(1.35, 103.87))
    }
    var addMarkerBoolean by remember {
        mutableStateOf(false)
    }

    val curLat by remember {
        mutableStateOf(LocationService.objLat)
    }
    val curLong by remember {
        mutableStateOf(LocationService.objLong)
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cairo, 15f)
    }
    Box(Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = properties,
            onMapClick = {
                currLatLong = it
                addMarkerBoolean = true

            }
        ) {



            MapEffect { map ->
                // map is the GoogleMap
                client.lastLocation.addOnSuccessListener { location:Location?->
                    if (location!=null){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),15f))
                    }
                }

                /*map.animateCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(LocationService.objLat.value, LocationService.objLong.value)
                    )
                )*/
            }
            if (addMarkerBoolean) {
                Marker(state = MarkerState(currLatLong), title = "I did it")


            }


            Marker(
                state = cairoState,
                title = "this is cairo"
            )
            showPoints()
        }
        val coroutinScope = rememberCoroutineScope()
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            Button(onClick = {

                    client.lastLocation.addOnSuccessListener { location:Location?->
                        if (location!=null){
                            coroutinScope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),15f))
                            }
                        }
                    }
             }, colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3C72),
                contentColor = Color.White
            ),) {
                Image(
                    painter = (painterResource(id = R.drawable.baseline_location_searching_24)),
                    contentDescription = ""
                )

            }
            Button(

                onClick = {
                    navController.navigate("addpoint")

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E3C72),
                    contentColor = Color.White
                ),
                ) {
                Text(text = "add point", fontSize = 20.sp)
            }

        }
        Button(
            modifier = Modifier.align(Alignment.BottomStart),

            onClick = {
                coroutinScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLng(
                            cairo
                        )
                    )
                }

            }
        ) {
            Text(text = "Animate camera to Cairo", color = Color.White)
        }

    }


}




