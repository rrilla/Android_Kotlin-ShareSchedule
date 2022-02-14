package com.example.share_schedule.insertCalendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.share_schedule.data.CalendarRepository
import com.example.share_schedule.data.db.entity.CalendarEntity
import com.example.share_schedule.data.remote.model.event.Event
import com.example.share_schedule.data.remote.model.event.InsertEventEntity
import com.example.share_schedule.insertCalendar.adapter.InsertReminderData
import com.google.api.client.util.DateTime
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEventViewModel: ViewModel() {

    private val calendarRepository = CalendarRepository()

    private var _calendarListLiveData = MutableLiveData<List<CalendarEntity>>()
    val calendarListLiveData: LiveData<List<CalendarEntity>> = _calendarListLiveData

    private var _selectCalendarLiveData = MutableLiveData<CalendarEntity>()
    val selectCalendarLiveData: LiveData<CalendarEntity> = _selectCalendarLiveData

    private var _startDateTimeLiveData = MutableLiveData<Calendar>()
    val startDateTimeLiveData: LiveData<Calendar> = _startDateTimeLiveData

    private var _endDateTimeLiveData = MutableLiveData<Calendar>()
    val endDateTimeLiveData: LiveData<Calendar> = _endDateTimeLiveData

    fun getCalendarList() = viewModelScope.launch{
        val calendarList = calendarRepository.getLocalCalendarList()
        setCalendarList(calendarList)
    }

    private fun setCalendarList(list: List<CalendarEntity>) {
        _calendarListLiveData.postValue(list)
    }

    fun setSelectCalendar(position: Int) {
        val selectCalendar = calendarListLiveData.value?.get(position)
        _selectCalendarLiveData.postValue(selectCalendar!!)
    }

    fun setStartDateTime(cal: Calendar) {
        _startDateTimeLiveData.postValue(cal)
    }
    fun setEndDateTime(cal: Calendar) {
        _endDateTimeLiveData.postValue(cal)
    }

    fun insertEvent(event: InsertEventEntity) = viewModelScope.launch {
        val startDateTime = startDateTimeLiveData.value?.time
        val endDateTime = endDateTimeLiveData.value?.time
        event.apply {
            this.calendarId = selectCalendarLiveData.value?.id
            this.startDateTime = DateTime(startDateTime)
            this.endDateTime = DateTime(endDateTime)
        }
        calendarRepository.insertEvent(event)
    }

}