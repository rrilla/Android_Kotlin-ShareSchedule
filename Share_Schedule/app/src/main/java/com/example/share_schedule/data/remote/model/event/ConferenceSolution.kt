package com.example.share_schedule.data.remote.model.event

import androidx.room.Embedded

data class ConferenceSolution(
    val iconUri: String?,
    @Embedded val key: Key?,
    val name: String?
)