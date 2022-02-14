package com.example.share_schedule.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.share_schedule.data.db.entity.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM EventEntity")
    suspend fun getEventListEntity(): List<EventEntity>

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    suspend fun getEventEntity(id: String): EventEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventEntity(eventList: List<EventEntity>)

    @Query("DELETE FROM EventEntity")
    suspend fun deleteAllEventEntity()

    @Query("SELECT * FROM EventEntity WHERE (start_date < :afterDate and end_endDate >= :beforeDate) OR (start_dateTime < :afterDate and end_endDateTime >= :beforeDate)")
    suspend fun getEventListEntityWithDate(beforeDate: String, afterDate: String): List<EventEntity>
}