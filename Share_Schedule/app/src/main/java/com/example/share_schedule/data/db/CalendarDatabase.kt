package com.example.share_schedule.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.share_schedule.data.db.dao.CalendarDao
import com.example.share_schedule.data.db.dao.EventDao
import com.example.share_schedule.data.db.entity.CalendarEntity
import com.example.share_schedule.data.db.entity.EventEntity
import com.example.share_schedule.data.db.entity.MyConverter

@Database(entities = [CalendarEntity::class, EventEntity::class], version = 1, exportSchema = false)
@TypeConverters(MyConverter::class)
abstract class CalendarDatabase: RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
    abstract fun eventDao(): EventDao

    companion object {
        const val DB_NAME = "CalendarDB.db"
    }
}