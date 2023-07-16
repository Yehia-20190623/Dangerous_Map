package com.example.dangerousmapv10.Map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.location.Location
import android.widget.Toast
import androidx.annotation.DrawableRes
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
import com.example.dangerousmapv10.API.AdminAPInterface
import com.example.dangerousmapv10.API.MapApiInterface
import com.example.dangerousmapv10.ApplicationAdmin
import com.example.dangerousmapv10.R
import com.example.dangerousmapv10.data.Point
import com.example.dangerousmapv10.data.Role
import com.example.dangerousmapv10.hasLocationPermission
import com.example.dangerousmapv10.isAdmin
import com.example.dangerousmapv10.localhost
import com.example.dangerousmapv10.nearestPoint
import com.example.dangerousmapv10.points
import com.example.dangerousmapv10.userApplication
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
suspend fun removeMarker(lat: Double, long: Double, context: Context) {
    val point = com.example.dangerousmapv10.data.Location()
    point.latitude = lat
    point.longitude = long
    val retrofit = Retrofit.Builder()
        .baseUrl("http://$localhost:8080/admin/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface: AdminAPInterface = retrofit.create(AdminAPInterface::class.java)
    val call: Call<Boolean> = apiInterface.removePoint(point)
    return suspendCoroutine { continuation ->
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    if (response.body()!!)
                        Toast.makeText(context, "The Point Removed", Toast.LENGTH_LONG).show()
                    continuation.resume(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    continuation.resumeWithException(Exception(errorBody))
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                print(t.message)
            }
        })
    }
}

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
    var markerLatitude by remember{ mutableStateOf(0.0) }
    var markerLongitude by remember{ mutableStateOf(0.0)}
    val context = LocalContext.current
    var isLocationEnabled by remember {
        mutableStateOf(context.hasLocationPermission())
    }
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
                    map.setOnMarkerClickListener { marker ->
                        markerLatitude = marker.position.latitude
                        markerLongitude = marker.position.longitude
                        true
                    }
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
            LaunchedEffect(showPoints() ){
                delay(1000L)
            }


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

            if (isAdmin.value){
                Button(
                    onClick = {
                        coroutinScope.launch {
                            removeMarker(markerLatitude,markerLongitude,context)
                        }
                        navController.navigate("map")
                        Toast.makeText(context,"$markerLatitude , $markerLongitude",Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E3C72),
                        contentColor = Color.White
                    ),
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.baseline_minimize_24)),
                        contentDescription = ""
                    )
                }
            }
            else{
                Button(

                    onClick = {
                        if(userApplication == null)
                            navController.navigate("login")
                        else
                            navController.navigate("addpoint")

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E3C72),
                        contentColor = Color.White
                    ),
                ) {
                    Image(
                        painter = (painterResource(id = R.drawable.baseline_add_24)),
                        contentDescription = ""
                    )
                }
//
                }

        }


    }


}

fun checkForGeoFenceEntry(
    userLocation: Location,
    point: Point,
    radius: Double,
): Boolean {

    val startLatLng = LatLng(userLocation.latitude, userLocation.longitude) // User Location
    val geofenceLatLng = LatLng(point.latitude, point.longitude) // Center of geofence

    val distanceInMeters = SphericalUtil
        .computeDistanceBetween(startLatLng, geofenceLatLng)

    if (distanceInMeters < radius) {
        if (nearestPoint != null) {
            if (point == nearestPoint) {
                return false
            }
        }

        nearestPoint = point
        return true
    }
    return false
}
@Composable
fun MapMarker(
    context: Context,
    position: LatLng,
    title: String,
    @DrawableRes iconResourceId: Int
) {
    val icon = bitmapDescriptorFromVector(
        context, iconResourceId
    )
    Marker(
        state = MarkerState(position = position),
        title = title,
        icon = icon,
    )
}
fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}
@Composable
fun showPoints() {
    val pointsState = remember { mutableStateOf<List<Point>>(emptyList()) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val points = getPoints()
        pointsState.value = points
    }

    points = pointsState.value
    if (points.isNotEmpty()) {
        for (point in points) {
            MapMarker(context = context, position = LatLng(point.latitude, point.longitude), title = "", iconResourceId =R.drawable.baseline_fmd_bad_24 )
            //Marker(state = MarkerState(LatLng(point.latitude, point.longitude)))

            MapEffect { map ->

                map.addCircle(
                    CircleOptions()
                        .center(LatLng(point.latitude, point.longitude))
                        .radius(150.0)
                        .fillColor(
                            ContextCompat.getColor(
                                context, when (point.dangerLevel.lowercase()) {
                                    "low" -> R.color.yellow_transparent
                                    "medium" -> R.color.orange_transparent
                                    "high" -> R.color.red_transparent
                                    else -> {
                                        R.color.orange_transparent
                                    }
                                }
                            )
                        )
                        .strokeColor(
                            ContextCompat.getColor(
                                context, when (point.dangerLevel.lowercase()) {
                                    "low" -> R.color.yellow
                                    "medium" -> R.color.orange
                                    "high" -> R.color.red
                                    else -> {
                                        R.color.orange
                                    }
                                }
                            )
                        )


                )
            }
        }
    }


}

suspend fun getPoints(): List<Point> {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://$localhost:8080/map/")
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

