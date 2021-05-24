package com.jinny.lookingforrooms

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoomsModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val title: String,
    @SerializedName("menuInfo")
    val price: String,
    @SerializedName("thumUrl")
    val imgUrl: String,
    @SerializedName("x")
    val lng: Double,
    @SerializedName("y")
    val lat: Double,
    @SerializedName("tel")
    val tel: String,
    @SerializedName("abbrAddress")
    val address: String,
    @SerializedName("bizhourInfo")
    val hourInfo: String,
    @SerializedName("hasNaverBooking")
    val hasBooking: Boolean,
    @SerializedName("naverBookingUrl")
    val bookingUrl: String
) : Serializable