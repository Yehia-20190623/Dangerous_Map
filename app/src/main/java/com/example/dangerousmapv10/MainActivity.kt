package com.example.dangerousmapv10

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color.WHITE
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.dangerousmapv10.ui.theme.DangerousMapV10Theme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

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
                    LoginPage()
                    //Map()
                }
            }
        }
    }
}

@Composable
fun Map() {

    val singapore = LatLng(1.35, 103.87)
    val sydney = LatLng(-33.852, 151.211)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        )
        Button(
            onClick = {
                cameraPositionState.move(CameraUpdateFactory.newLatLng(sydney))
            }
        ) {
            Text(text = "Animate camera to Sydney")
        }
    }
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3C72),
            contentColor = Color.White),
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