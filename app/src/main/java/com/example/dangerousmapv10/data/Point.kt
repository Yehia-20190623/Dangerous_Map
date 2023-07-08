package com.example.dangerousmapv10.data
enum class Level{
    LOW,MEDIUM,HIGH
}
enum class Type{
    SPEED_BUMP,HOLE,WORK_AREA
}


class Point {
    var userid = 0
    var dangerLevel: String = ""
    var dangerousType: String = ""
    var description: String = ""
    var latitude = 0.0
    var longitude = 0.0

}
