package com.example.dangerousmapv10.API


import com.example.dangerousmapv10.data.User
import retrofit2.Call

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SystemApiInterface {
    @POST("signup")
    fun signup(@Body user: User): Call<Boolean>;
    @GET("login/{id}")
    fun getUser(@Path ("id") id:String):Call<User>;
    @GET("logout")
    fun logout():Call<Boolean>;
}