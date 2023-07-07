package com.example.dangerousmapv10.data

import retrofit2.http.GET

enum class Role{
    USER,ADMIN
}
class User {
    var name:String=""
    var email:String=""
    var password:String=""
    var role:Role=Role.USER

}