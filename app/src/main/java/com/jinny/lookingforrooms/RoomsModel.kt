package com.jinny.lookingforrooms

data class RoomsModel(
    val id: Int,
    val title: String,
    val price: String,
    val imgUrl: String,
    val lat: Double,
    val lng: Double
)