package com.example.dangerousmapv10.data
enum class Level{
    LOW,MEDIUM,HIGH
}
enum class Type{
    SPEED_BUMP,HOLE,WORK_AREA
}


class Point {
    var id:Int = 0
    var dangerousLevel:Level=Level.LOW
    var dangerousType:Type=Type.SPEED_BUMP
    var description:String=""
    var latitude = 0.0
    var longitude = 0.0
}
