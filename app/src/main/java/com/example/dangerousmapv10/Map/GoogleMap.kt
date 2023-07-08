package com.example.dangerousmapv10.Map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.dangerousmapv10.API.MapApiInterface
import com.example.dangerousmapv10.R
import com.example.dangerousmapv10.data.Point
import com.example.dangerousmapv10.data.Role
import com.example.dangerousmapv10.hasLocationPermission
import com.example.dangerousmapv10.isAdmin
import com.example.dangerousmapv10.points
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context): Location? {
    val client = LocationServices.getFusedLocationProviderClient(context)
    client.lastLocation.addOnSuccessListener { location: Location? ->
        return@addOnSuccessListener
    }
    return null
}


@SuppressLint("MissingPermission")
@Composable
fun Map(modifier: Modifier, navController: NavController) {
    val context = LocalContext.current
    var isLocationEnabled by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    var map: GoogleMap
    val coroutinScope = rememberCoroutineScope()
    val client = LocationServices.getFusedLocationProviderClient(context)
    var uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    var properties by remember {
        mutableStateOf(
            if (isLocationEnabled) {
                isLocationEnabled = context.hasLocationPermission()
                MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = true
                )
            } else {
                isLocationEnabled = context.hasLocationPermission()
                MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = false
                )
            }

        )
    }
    val cairo = LatLng(30.0444, 31.2357)
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

            }
        ) {


            MapEffect { map ->
                // map is the
                if (context.hasLocationPermission()) {
                    client.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    ), 15f
                                )
                            )

                        }
                    }

                }


            }

            showPoints()
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            Button(
                onClick = {
                    if (context.hasLocationPermission()) {
                        client.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                coroutinScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(location.latitude, location.longitude),
                                            15f
                                        )
                                    )
                                }
                            }

                        }

                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E3C72),
                    contentColor = Color.White
                ),
            ) {
                Image(
                    painter = (painterResource(id = R.drawable.baseline_location_searching_24)),
                    contentDescription = ""
                )

            }
           /* if (isAdmin.equals(Role.ADMIN)){
                Button(

                    onClick = {



                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E3C72),
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "removePoint", fontSize = 20.sp)
                }
            }
            else{
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

            }*/
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


    }


}

fun checkForGeoFenceEntry(
    userLocation: Location,
    geofenceLat: Double,
    geofenceLong: Double,
    radius: Double,
) {
    val startLatLng = LatLng(userLocation.latitude, userLocation.longitude) // User Location
    val geofenceLatLng = LatLng(geofenceLat, geofenceLong) // Center of geofence

    val distanceInMeters = SphericalUtil.computeDistanceBetween(startLatLng, geofenceLatLng)

    if (distanceInMeters < radius) {
        // User is inside the Geo-fence

    }
}


@Composable
fun showPoints() {
    val pointsState = remember { mutableStateOf<List<Point>>(emptyList()) }

    LaunchedEffect(Unit) {
        val points = getPoints()
        pointsState.value = points
    }

    val points = pointsState.value
    if (points.isNotEmpty()) {
        for (point in points) {
            setMarker(lat = point.latitude, long = point.longitude)
        }
    }

    /*

        points= getPoints()
        if (points != null) {
            for (i in points!!){
                setMarker(lat = i.latitude, long = i.longitude)
                MapEffect{
                    map->
                    map.addCircle(
                        CircleOptions()
                            .center(LatLng(i.latitude,i.longitude))
                            .radius(150.0)
                            .fillColor(ContextCompat.getColor(context, R.color.teal_200))
                            .strokeColor(ContextCompat.getColor(context, R.color.teal_200))

                    )
                }

            }
        }*/

}

suspend fun getPoints(): List<Point> {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/map/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface: MapApiInterface = retrofit.create(MapApiInterface::class.java)
    val call: Call<List<Point>> = apiInterface.getPoints()
    return suspendCoroutine { continuation ->
        call.enqueue(object : Callback<List<Point>> {
            override fun onResponse(call: Call<List<Point>>, response: Response<List<Point>>) {
                println("Response returned")
                val body = response.body()
                if (body != null) {
                    continuation.resume(body)
                } else {
                    continuation.resume(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Point>>, t: Throwable) {
                println("Request failed")
                continuation.resume(emptyList())
            }
            })
        }

}

@Composable
fun setMarker(lat: Double, long: Double) {
    val point = LatLng(lat, long)
    Marker(state = MarkerState(point), title = "")

}
