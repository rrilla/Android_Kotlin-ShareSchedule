package com.example.share_schedule.data.remote.model.event

import com.google.gson.annotations.SerializedName

data class ConferenceSolutionKey(
    @SerializedName("type") val solutionKeyType: String?
)