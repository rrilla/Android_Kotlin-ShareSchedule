package com.example.share_schedule.data.remote.model.event

import com.example.share_schedule.data.remote.model.event.Override

data class Reminders(
    val overrides: List<Override>?,
    val useDefault: Boolean?
)