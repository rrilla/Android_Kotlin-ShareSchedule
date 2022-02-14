package com.example.share_schedule.data.remote.model.event

import androidx.room.Embedded

data class CreateRequest(
    @Embedded val conferenceSolutionKey: ConferenceSolutionKey?,
    val requestId: String?,
    @Embedded val status: Status?
)