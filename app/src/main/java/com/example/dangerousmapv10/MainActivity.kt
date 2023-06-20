@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dangerousmapv10


import android.location.LocationManager
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dangerousmapv10.ui.theme.Black
import com.example.dangerousmapv10.ui.theme.DangerousMapV10Theme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


//import androidx.compose.ui.text.input.PasswordTextField

var locationManager: LocationManager? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DangerousMapV10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    //LoginPage()
                    Map()
                }
            }
        }
    }
}


@Composable

fun Map() {
    var uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    var properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    val singapore = LatLng(1.35, 103.87)
    val cairo = LatLng(30.0444, 31.2357)
    val cairoState = MarkerState(position = cairo)
    var currLatLong by remember {
        mutableStateOf(LatLng(1.35, 103.87))
    }
    var addMarkerBoolean by remember {
        mutableStateOf(false)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {


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
                if (addMarkerBoolean) {
                    Marker(state = MarkerState(currLatLong), title = "I did it")


                }


                Marker(
                    state = cairoState,
                    title = "this is cairo"
                )
            }
            val coroutinScope = rememberCoroutineScope()
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),

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

}

@Composable
fun addMarker(myloc: MarkerState) {

    Marker(state = myloc, title = "onClick")

}

@Composable
fun LoginPage() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Login",
                color = Color.Black,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))
            val labelWidth = 80.dp
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Email: ",
                    color = Color.Black,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        containerColor = Color(0xFFD3D3D3),
                        unfocusedLabelColor = Color(0xFF696969),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Password: ",
                    color = Color.Black,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        containerColor = Color(0xFFD3D3D3),
                        unfocusedLabelColor = Color(0xFF696969),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
        Button(
            onClick = { /* perform login */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3C72),
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(60.dp)

        ) {
            Text(text = "Login", fontSize = 24.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MapPreview() {
    DangerousMapV10Theme {
        Map()
    }
}