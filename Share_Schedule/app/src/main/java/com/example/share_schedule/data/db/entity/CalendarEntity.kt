package com.example.share_schedule.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.share_schedule.data.remote.model.calendar.ConferenceProperties
import com.example.share_schedule.data.remote.model.calendar.DefaultReminder
import com.example.share_schedule.data.remote.model.calendar.NotificationSettings


@Entity(tableName = "CalendarEntity")
data class CalendarEntity(
    val accessRole: String?,
    val backgroundColor: String?,
    val colorId: String?,
    @Embedded val conferenceProperties: ConferenceProperties?,
    val defaultReminders: List<DefaultReminder>?,
    val description: String?,
    val etag: String?,
    val foregroundColor: String?,
    @PrimaryKey val id: String,
    val kind: String?,
    @Embedded val notificationSettings: NotificationSettings?,
    val primary: Boolean?,
    val selected: Boolean?,
    val summary: String?,
    val summaryOverride: String?,
    val timeZone: String?
)