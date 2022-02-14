package com.example.share_schedule.data

import com.example.share_schedule.data.db.entity.CalendarEntity
import com.example.share_schedule.data.db.entity.EventEntity
import com.example.share_schedule.data.remote.model.event.InsertEventEntity

interface CalendarDataSource {

    suspend fun getCalendarList(): List<CalendarEntity>?

    suspend fun getLocalCalendarList(): List<CalendarEntity>

    suspend fun getEventList(calendarId: String): List<EventEntity>

    suspend fun insertEvent(insertEvent: InsertEventEntity): Boolean

    suspend fun getLocalEventListWithDate(beforeDate: String, afterDate: String): List<EventEntity>

    suspend fun insertLocalCalendarList(calendarList: List<CalendarEntity>)

    suspend fun insertLocalEventList(eventList: List<EventEntity>)

    suspend fun getLocalEventList(): List<EventEntity>

    suspend fun deleteAllCalendar()

    suspend fun deleteAllEvent()
}