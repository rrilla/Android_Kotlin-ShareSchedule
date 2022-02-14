package com.example.share_schedule.data.remote.model.event

import com.example.share_schedule.insertCalendar.adapter.InsertReminderData
import com.google.api.client.util.DateTime

class InsertEventEntity {
    var summary: String? = null
    var location: String? = null
    var description: String? = null
    var startDateTime: DateTime? = null
    var endDateTime: DateTime? = null
    var attendees: List<String>? = null
    var reminders: List<InsertReminderData>? = null
    var calendarId: String? = null
}