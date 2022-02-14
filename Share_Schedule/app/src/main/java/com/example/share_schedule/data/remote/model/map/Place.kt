package com.example.share_schedule.data.remote.model.map

import com.google.gson.annotations.SerializedName

data class Place(
    val name: String,
    @SerializedName("place_id")
    val placeId: String,
    val userRatingsTotal: Int,
    val geometry: Geometry,
    val vicinity: String,
    val rating: Float?
)
