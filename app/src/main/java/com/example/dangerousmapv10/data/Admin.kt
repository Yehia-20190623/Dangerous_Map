package com.example.dangerousmapv10.data

import com.example.dangerousmapv10.API.AdminAPInterface
import retrofit2.http.GET

abstract class Admin {
    var name:String=""
    var email:String=""
    var password:String=""
    var role:Role=Role.ADMIN
    abstract var adminAPInterface:AdminAPInterface
}