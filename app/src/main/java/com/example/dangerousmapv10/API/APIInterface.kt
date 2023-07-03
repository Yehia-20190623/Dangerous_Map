package com.example.dangerousmapv10.API


import com.example.dangerousmapv10.Data.Point
import retrofit2.http.GET

interface APInterface {
    @GET("show")
    fun getPoints():retrofit2.Call<List<Point>>
}
