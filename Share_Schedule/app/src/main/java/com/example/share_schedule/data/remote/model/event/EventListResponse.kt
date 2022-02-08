package com.example.share_schedule.data.remote.model.event

import com.example.share_schedule.data.remote.model.event.DefaultReminder
import com.example.share_schedule.data.remote.model.event.Event
import com.google.gson.annotations.SerializedName

data class EventListResponse(
    val accessRole: String?,
    val defaultReminders: List<DefaultReminder>?,
    val etag: String?,
    @SerializedName("items") val events: List<Event>?,
    val kind: String?,
    val nextSyncToken: String?,
    val summary: String?,
    val timeZone: String?,
    val updated: String?
)