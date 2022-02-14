package com.example.share_schedule.data.remote.model.calendar

import com.example.share_schedule.data.db.entity.CalendarEntity

data class Calendar(
    val accessRole: String?,
    val backgroundColor: String?,
    val colorId: String?,
    val conferenceProperties: ConferenceProperties?,
    val defaultReminders: List<DefaultReminder>?,
    val description: String?,
    val etag: String?,
    val foregroundColor: String?,
    val id: String,
    val kind: String?,
    val notificationSettings: NotificationSettings?,
    val primary: Boolean?,
    val selected: Boolean?,
    val summary: String?,
    val summaryOverride: String?,
    val timeZone: String?
) {
    fun toEntity(): CalendarEntity =
        CalendarEntity(
            accessRole = accessRole,
            backgroundColor = backgroundColor,
            colorId = colorId,
            conferenceProperties = conferenceProperties,
            defaultReminders = defaultReminders,
            description = description,
            etag = etag,
            foregroundColor = foregroundColor,
            id = id,
            kind = kind,
            notificationSettings = notificationSettings,
            primary = primary,
            selected = selected,
            summary = summary,
            summaryOverride = summaryOverride,
            timeZone = timeZone
        )
}