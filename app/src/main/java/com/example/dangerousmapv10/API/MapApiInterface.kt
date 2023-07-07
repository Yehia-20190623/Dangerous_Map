package com.example.dangerousmapv10.API

import com.example.dangerousmapv10.data.Point
import retrofit2.http.GET

interface MapApiInterface {
    @GET("show")
    fun getPoints():retrofit2.Call<List<Point>>
}