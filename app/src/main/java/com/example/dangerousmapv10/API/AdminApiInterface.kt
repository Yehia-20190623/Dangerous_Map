package com.example.dangerousmapv10.API
import com.example.dangerousmapv10.data.Location

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
interface AdminAPInterface{
    @POST("removeById")
    fun removePoint(pointId:Int)

    @POST ("removePoint")
    fun removePoint(@Body point : Location):Call<Boolean>
}