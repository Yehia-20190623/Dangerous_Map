package com.example.dangerousmapv10.data

import android.content.Context
import android.widget.Toast
import com.example.dangerousmapv10.API.AdminAPInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class Admin {
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var role: String = ""

}