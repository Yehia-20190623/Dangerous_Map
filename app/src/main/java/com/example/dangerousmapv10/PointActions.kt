@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.dangerousmapv10

import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dangerousmapv10.API.UserApiInterface
import com.example.dangerousmapv10.data.Point
import com.example.dangerousmapv10.ui.theme.Black
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
@ExperimentalMaterial3Api
@Composable
fun AddPointToMap(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val problemtype = arrayOf("hole", "bump", "work area")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(problemtype[0]) }
    val problemlevel = arrayOf("high", "medium", "low")
    var selectedlevel by remember { mutableStateOf(problemlevel[0]) }
    var expanded1 by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3B71))

    ) {


        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(

                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = !expanded) },

                    )
                ExposedDropdownMenu(
                    expanded = expanded,

                    onDismissRequest = { expanded = false }

                ) {
                    problemtype.forEach { problem ->
                        DropdownMenuItem(

                            text = { Text(text = problem) },
                            onClick = {
                                selectedText = problem
                                expanded = false
                                Toast.makeText(context, problem, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            ExposedDropdownMenuBox(
                expanded = expanded1,
                onExpandedChange = {
                    expanded1 = !expanded1
                }
            ) {
                TextField(

                    value = selectedlevel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = !expanded1) },

                    )
                ExposedDropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false }
                ) {
                    problemlevel.forEach { problem ->
                        DropdownMenuItem(

                            text = { Text(text = problem) },
                            onClick = {
                                selectedlevel = problem
                                expanded1 = false
                                Toast.makeText(context, problem, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            val labelWidth = 100.dp
            Spacer(modifier = Modifier.height(50.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                //disc
                TextField(

                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        containerColor = Color.White,
                        unfocusedLabelColor = Color(0xFF696969),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Button(

                onClick = {

                    if (context.hasLocationPermission()) {

                        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            var latitude = 0.0
                            var longitude = 0.0
                            if (location != null) {
                                latitude = location.latitude
                                longitude = location.longitude
                            }
                            coroutineScope.launch {
                                addPoint(
                                    latitude,
                                    longitude,
                                    description,
                                    selectedlevel,
                                    selectedText,
                                    6668
                                )
                            }
                            navController.navigate("map")
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Go to App Settings to allow accessing location",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1D3B71)
                ),


                ) {
                Text(text = "add point", fontSize = 24.sp)
            }


        }


    }
}

suspend fun addPoint(
    latitude: Double,
    longitude: Double,
    description: String,
    dangerLevel: String,
    dangerousType: String,
    userid: Int,
) {
    val point = Point()
    point.latitude = latitude
    point.longitude = longitude
    point.description = description
    point.dangerLevel = dangerLevel
    point.dangerousType = dangerousType
    point.userid = userid
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/user/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface: UserApiInterface = retrofit.create(UserApiInterface::class.java)
    val call: Call<Response<Point>> = apiInterface.addPoints(point)
    return suspendCoroutine { continuation ->
        call.enqueue(object : Callback<Response<Point>> {
            override fun onResponse(
                call: Call<Response<Point>>,
                response: Response<Response<Point>>,
            ) {
                response.headers().get("")
                val status = response.code()
                print(status)
            }

            override fun onFailure(call: Call<Response<Point>>, t: Throwable) {
                print("Failed")
            }
        })
    }
}