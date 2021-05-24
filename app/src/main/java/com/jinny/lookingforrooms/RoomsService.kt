package com.jinny.lookingforrooms

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RoomsService {
    @GET("/v5/api/search?caller=")
    fun getRoomsList(
        @Query("query") query: String,
        @Query("displayCount") displayCount:Int = 20
    ): Call<RoomsResponse>
}