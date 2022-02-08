package com.example.share_schedule.data.remote.model.calendar

import com.example.share_schedule.data.remote.model.calendar.Calendar
import com.google.gson.annotations.SerializedName

data class CalendarListResponse(
    @SerializedName("etag") val etag: String,
    @SerializedName("item") val calendars: List<Calendar>,
    @SerializedName("kind") val kind: String,
    @SerializedName("nextSyncToken") val nextSyncToken: String
)