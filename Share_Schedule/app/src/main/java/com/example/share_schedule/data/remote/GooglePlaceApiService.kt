package com.example.share_schedule.data.remote

import com.example.share_schedule.data.remote.model.map.ResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlaceApiService {

    val sameParam: String
        get() = "type=restaurant&language=ko&"

    @GET("nearbysearch/json?rankby=prominence&radius=2000&language=ko")
    fun searchByProminence(
        @Query("key") key: String,
        @Query("location") location: String,
        @Query("type") type: String
    ): Call<ResponseModel>

    @GET("nearbysearch/json?rankby=distance&language=ko")
    fun searchByDistance(
        @Query("key") key: String,
        @Query("location") location: String,
        @Query("type") type: String
    ): Call<ResponseModel>
}