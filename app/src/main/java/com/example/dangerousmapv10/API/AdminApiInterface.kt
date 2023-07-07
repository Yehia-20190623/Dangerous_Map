package com.example.dangerousmapv10.API
import com.example.dangerousmapv10.data.Point
import retrofit2.http.POST
interface AdminAPInterface{
    @POST("removeById")
    fun removePoint(pointId:Int)
    @POST ("removePoint")
    fun removePoint(latitude:Double,longitude:Double)
}
