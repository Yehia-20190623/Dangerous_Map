package com.example.dangerousmapv10.API

import com.example.dangerousmapv10.data.Point
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiInterface {
    @POST("addPoint")
    fun addPoints(@Body point: Point):Call<Response<Point>>;
}