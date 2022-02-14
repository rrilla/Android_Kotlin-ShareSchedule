package com.example.share_schedule.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.share_schedule.data.remote.model.event.*

@Entity(tableName = "EventEntity")
data class EventEntity(
    val attendees: List<Attendee>?,
    val colorId: String?,
    @Embedded val conferenceData: ConferenceData?,
    val created: String?,
    @Embedded val creator: Creator?,
    val description: String?,
    @Embedded(prefix = "end_") val end: End?,
    val etag: String?,
    val eventType: String?,
    val guestsCanInviteOthers: Boolean?,
    val guestsCanSeeOtherGuests: Boolean?,
    val hangoutLink: String?,
    val htmlLink: String?,
    val iCalUID: String?,
    @PrimaryKey val id: String,
    val kind: String?,
    val location: String?,
    @Embedded val organizer: Organizer?,
    val recurrence: List<String>?,
    @Embedded val reminders: Reminders?,
    val sequence: Int?,
    @Embedded(prefix = "start_") val start: Start?,
    val status: String?,
    val summary: String ?= "제목없음",
    val transparency: String?,
    val updated: String?,
    val visibility: String?,
    val calendarId: String?
)