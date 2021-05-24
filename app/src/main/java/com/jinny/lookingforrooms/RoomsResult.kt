package com.jinny.lookingforrooms

import com.google.gson.annotations.SerializedName

data class RoomsResult(
    @SerializedName("place")
    var place : RoomsList
)