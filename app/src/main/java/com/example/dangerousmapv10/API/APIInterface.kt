package com.example.dangerousmapv10.API


import com.example.dangerousmapv10.Data.Point
import retrofit2.http.GET

interface APInterface {
    @get:GET("show")
    val points: retrofit2.Call<List<Point>>
}
