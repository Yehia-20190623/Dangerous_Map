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
import androidx.navigation.NavController
import com.example.dangerousmapv10.API.APInterface
import com.example.dangerousmapv10.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    val coroutinScope = rememberCoroutineScope()
    val client=LocationServices.getFusedLocationProviderClient(LocalContext.current)
    var uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    var properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            )
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
                // map is the GoogleMap
                client.lastLocation.addOnSuccessListener { location:Location?->
                    if (location!=null){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),15f))
                    }
                }

            }

            showPoints()
        }

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



    }


}





@Composable
fun showPoints(){
    val points= getPoints()
    if (points != null) {
        for (i in points){
            setMarker(lat = i.latitude, long = i.longitude)

        }
    }

}

fun getPoints(): List<com.example.dangerousmapv10.Data.Point>? {
    var points: List<com.example.dangerousmapv10.Data.Point>? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/map/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface: APInterface = retrofit.create(APInterface::class.java)
    val call: Call<List<com.example.dangerousmapv10.Data.Point>> = apiInterface.getPoints()
    call.enqueue(object : Callback<List<com.example.dangerousmapv10.Data.Point>> {

        override fun onResponse(call: Call<List<com.example.dangerousmapv10.Data.Point>>, response: Response<List<com.example.dangerousmapv10.Data.Point>>) {
            print("Response returned")
            var result = ""
            points= response.body()
            for(i in points!!){
                print(i.latitude)
            }
        }

        override fun onFailure(call: Call<List<com.example.dangerousmapv10.Data.Point>>, t: Throwable) {
            print("Request failed")
        }
    })
    return points
}
@Composable
fun setMarker(lat:Double,long:Double){
    val point=LatLng(lat,long)
    Marker(state = MarkerState(point), title = "")

}
