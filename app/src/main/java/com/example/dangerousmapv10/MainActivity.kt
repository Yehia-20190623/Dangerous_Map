@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dangerousmapv10


import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class)
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

                    //var drawerState = rememberDrawerState(initialValue =  DrawerValue.Closed)
                    var drawerState by remember {
                        mutableStateOf(DrawerState(initialValue = DrawerValue.Closed))
                    }
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            NavigationDrawerItem(
                                label = { Row(modifier = Modifier.fillMaxWidth()) {
                                    Image (painter = (painterResource(id = R.drawable.baseline_arrow_back_24)), contentDescription = "")
                                    Text(text = "Back")

                                }
                                     },
                                selected = false,
                                onClick = {
                                    drawerState = DrawerState(initialValue = DrawerValue.Closed)
                                })
                            NavigationDrawerItem(
                                label = {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image (painter = (painterResource(id = R.drawable.baseline_settings_24)), contentDescription = "")
                                        Text(text = "Settings")
                                    }

                                },
                                selected = false,
                                onClick = { })
                            NavigationDrawerItem(
                                label = {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image (painter = (painterResource(id = R.drawable.baseline_share_24)), contentDescription = "")
                                        Text(text = "Share")
                                    }

                                },
                                selected = false,
                                onClick = { })
                            NavigationDrawerItem(
                                label = {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image (painter = (painterResource(id = R.drawable.baseline_logout_24)), contentDescription = "")
                                        Text(text = "Log Out")
                                    }

                                },
                                selected = false,
                                onClick = { })
                        },
                        gesturesEnabled = false,


                        ) {
                        Scaffold(topBar = {
                            SmallTopAppBar(
                                title = { Text(text = "My app") },
                                navigationIcon = {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "none",
                                        modifier = Modifier.clickable {
                                            drawerState =
                                                DrawerState(initialValue = DrawerValue.Open)
                                        }
                                    )

                                },
                            )
                        }) { contentPadding ->
                            Map(Modifier.padding(contentPadding))
                        }
                    }

                    //LoginPage()

                }
            }
        }
    }
}


@Composable

fun Map(modifier: Modifier) {
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
        position = CameraPosition.fromLatLngZoom(cairo, 10f)
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
            onClick = {
                print(email)
                print(password)
            },
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
        Map(Modifier)
    }
}