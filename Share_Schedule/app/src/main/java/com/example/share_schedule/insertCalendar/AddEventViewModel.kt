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

    //  Google Calendar에서 요구하는 RFC3339형식으로 변경
    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'")
        return dateFormat.format(date)
    }

    private fun setCalendarList(list: List<CalendarEntity>) {
        _calendarListLiveData.postValue(list)
    }

    fun setSelectCalendar(position: Int) {
        val selectCalendar = calendarListLiveData.value?.get(position)
        _selectCalendarLiveData.postValue(selectCalendar!!)
    }

    fun setStartDateTime(cal: Calendar) {
        Log.e("setStartDate", formatDate(cal.time))
        _startDateTimeLiveData.postValue(cal)
    }
    fun setEndDateTime(cal: Calendar) {
        Log.e("setEndDate", "time - ${cal.time}")
        _endDateTimeLiveData.postValue(cal)
    }

    fun insertEvent(event: InsertEventEntity) = viewModelScope.launch {
        val startDateTime = startDateTimeLiveData.value?.let { formatDate(it.time) }
        val endDateTime = endDateTimeLiveData.value?.let { formatDate(it.time) }
        event.apply {
            this.calendarId = selectCalendarLiveData.value?.id
            this.startDateTime = DateTime(startDateTime)
            this.endDateTime = DateTime(endDateTime)
            this.location = "테스트위치"
        }
        calendarRepository.insertEvent(event)
    }

//    private fun getStartDateTime() {
//        startDateLiveData.value.set(Calendar.HOUR_OF_DAY)
//    }
}