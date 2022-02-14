package com.example.share_schedule.data.remote.model.event

import com.google.gson.annotations.SerializedName

data class End(
    @SerializedName("date") val endDate: String?,
    @SerializedName("dateTime") val endDateTime: String?,
    @SerializedName("timeZone") val endTimeZone: String?
)