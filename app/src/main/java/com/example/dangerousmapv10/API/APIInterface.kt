package com.example.dangerousmapv10.API


import androidx.core.location.LocationRequestCompat.Quality
import com.example.dangerousmapv10.data.Point
import retrofit2.http.GET
import retrofit2.http.Query

interface AdminAPInterface {

    fun removePoint()
}
interface UserAPInterface {
    @GET("show")
    fun getPoints():retrofit2.Call<List<Point>>
}

interface APInterface {
    fun addPoint()
}
