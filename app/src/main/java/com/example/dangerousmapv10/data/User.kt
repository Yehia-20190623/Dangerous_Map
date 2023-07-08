package com.example.dangerousmapv10.data



enum class Role{
    USER,ADMIN
}
class User {
    var name:String=""
    var email:String=""
    var password:String=""
    var role:Role=Role.USER

}