package com.example.share_schedule.data.db.entity

import androidx.room.TypeConverter
import com.example.share_schedule.data.remote.model.calendar.DefaultReminder
import com.example.share_schedule.data.remote.model.calendar.Notification
import com.example.share_schedule.data.remote.model.event.Attendee
import com.example.share_schedule.data.remote.model.event.EntryPoint
import com.example.share_schedule.data.remote.model.event.Override
import com.google.gson.Gson

class MyConverter {
    @TypeConverter
    fun fromDefaultRemindersList(value: List<DefaultReminder>?): String = Gson().toJson(value)
    @TypeConverter
    fun reminderToStringList(value: String) = Gson().fromJson(value, Array<DefaultReminder>::class.java)?.toList()

    @TypeConverter
    fun fromStringList(value: List<String>?): String = Gson().toJson(value)
    @TypeConverter
    fun toStringList(value: String) = Gson().fromJson(value, Array<String>::class.java)?.toList()

    @TypeConverter
    fun fromNotificationList(value: List<Notification>?): String = Gson().toJson(value)
    @TypeConverter
    fun notiToStringList(value: String) = Gson().fromJson(value, Array<Notification>::class.java)?.toList()

    @TypeConverter
    fun fromAttendeeList(value: List<Attendee>?): String = Gson().toJson(value)
    @TypeConverter
    fun attendeeToStringList(value: String) = Gson().fromJson(value, Array<Attendee>::class.java)?.toList()

    @TypeConverter
    fun fromEntryPointList(value: List<EntryPoint>?): String = Gson().toJson(value)
    @TypeConverter
    fun entryPointToStringList(value: String) = Gson().fromJson(value, Array<EntryPoint>::class.java)?.toList()

    @TypeConverter
    fun fromOverrideList(value: List<Override>?): String = Gson().toJson(value)
    @TypeConverter
    fun overrideToStringList(value: String) = Gson().fromJson(value, Array<Override>::class.java)?.toList()


}