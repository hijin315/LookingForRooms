package com.jinny.lookingforrooms

import com.google.gson.annotations.SerializedName

data class RoomsResponse(
    @SerializedName("result")
    var roomsResult: RoomsResult
)