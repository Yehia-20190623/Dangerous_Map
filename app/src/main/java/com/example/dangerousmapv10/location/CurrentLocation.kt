package com.example.dangerousmapv10.location


import androidx.compose.runtime.mutableStateOf


object currentLocation  {
    var lat= mutableStateOf<Double>(0.0)
    var long= mutableStateOf<Double>(0.0)
}