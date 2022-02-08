package com.example.share_schedule.data.remote.model.event

import androidx.room.Embedded

data class ConferenceData(
    val conferenceId: String?,
    @Embedded val conferenceSolution: ConferenceSolution?,
    @Embedded val createRequest: CreateRequest?,
    val entryPoints: List<EntryPoint>?,
    val signature: String?
)