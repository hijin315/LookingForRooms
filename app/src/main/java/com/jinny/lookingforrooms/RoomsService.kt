package com.jinny.lookingforrooms

import retrofit2.Call
import retrofit2.http.GET

interface RoomsService {
    @GET("/v3/77babf80-d608-4c24-a5a6-edb19190a43e")
    fun getRoomsList() : Call<RoomsDto>
}