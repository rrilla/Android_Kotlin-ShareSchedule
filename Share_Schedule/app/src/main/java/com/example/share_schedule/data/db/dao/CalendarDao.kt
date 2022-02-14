package com.example.share_schedule.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.share_schedule.data.db.entity.CalendarEntity

@Dao
interface CalendarDao {

    @Query("SELECT * FROM calendarEntity")
    suspend fun getCalendarListEntity(): List<CalendarEntity>

    @Query("SELECT * FROM calendarEntity WHERE id = :id")
    suspend fun getCalendarEntity(id: String): CalendarEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEntity(Calendar: CalendarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEntity(CalendarList: List<CalendarEntity>)

    @Query("DELETE FROM CalendarEntity")
    suspend fun deleteAllCalendarEntity()
}