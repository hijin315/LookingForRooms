package com.jinny.lookingforrooms

import com.google.gson.annotations.SerializedName

data class RoomsList(
    @SerializedName("list")
    val items: List<RoomsModel> = arrayListOf<RoomsModel>()
)