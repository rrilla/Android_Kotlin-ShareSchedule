package com.example.share_schedule.data.remote.model.event

data class Attendee(
    val displayName: String?,
    val email: String?,
    val organizer: Boolean?,
    val responseStatus: String?,
    val self: Boolean?
)