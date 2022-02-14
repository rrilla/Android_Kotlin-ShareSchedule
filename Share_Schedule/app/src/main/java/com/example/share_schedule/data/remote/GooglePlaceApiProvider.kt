package com.example.share_schedule.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GooglePlaceApiProvider {

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val googlePlaceApiService: GooglePlaceApiService = retrofit.create(GooglePlaceApiService::class.java)
}