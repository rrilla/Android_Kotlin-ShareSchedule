package com.example.share_schedule.insertCalendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.share_schedule.data.CalendarRepository
import com.example.share_schedule.data.db.entity.CalendarEntity
import com.example.share_schedule.insertCalendar.adapter.InsertReminderData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEventViewModel: ViewModel() {

    private val calendarRepository = CalendarRepository()

    private var _calendarListLiveData = MutableLiveData<List<CalendarEntity>>()
    val calendarListLiveData: LiveData<List<CalendarEntity>> = _calendarListLiveData

    private var _summaryLiveData = MutableLiveData<String>()
    val summaryLiveData: LiveData<String> = _summaryLiveData

    private var _selectCalendarLiveData = MutableLiveData<CalendarEntity>()
    val selectCalendarLiveData: LiveData<CalendarEntity> = _selectCalendarLiveData

    private var _startDateLiveData = MutableLiveData<Calendar>()
    val startDateLiveData: LiveData<Calendar> = _startDateLiveData

    private var _startTimeLiveData = MutableLiveData<Calendar>()
    val startTimeLiveData: LiveData<Calendar> = _startTimeLiveData

    private var _endDateLiveData = MutableLiveData<Calendar>()
    val endDateLiveData: LiveData<Calendar> = _endDateLiveData

    private var _endTimeLiveData = MutableLiveData<Calendar>()
    val endTimeLiveData: LiveData<Calendar> = _endTimeLiveData

    private var _locationLiveData = MutableLiveData<String>()
    val locationLiveData: LiveData<String> = _locationLiveData

    private var _userLiveData = MutableLiveData<List<String>>()
    val userLiveData: LiveData<List<String>> = _userLiveData

    private var _reminderLiveData = MutableLiveData<List<InsertReminderData>>()
    val reminderLiveData: LiveData<List<InsertReminderData>> = _reminderLiveData

    private var _descriptionLiveData = MutableLiveData<String>()
    val descriptionLiveData: LiveData<String> = _descriptionLiveData

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
        for(calendar in list){
            Log.e("setCalendarList", "${calendar.id}${calendar.summary}")
        }
        _calendarListLiveData.postValue(list)
    }

    fun setSelectCalendar(position: Int) {
        val selectCalendar = calendarListLiveData.value?.get(position)
        _selectCalendarLiveData.postValue(selectCalendar!!)
    }

    fun setStartDate(cal: Calendar) {
        formatDate(cal.time)
        Log.e("setStartDate", formatDate(cal.time))
        _startDateLiveData.postValue(cal)
    }
    fun setEndDate(cal: Calendar) {
        Log.e("setEndDate", "time - ${cal.time}")
        _endDateLiveData.postValue(cal)
    }
    fun setStartTime(cal: Calendar) {
        Log.e("setStartTime", "time - ${cal.time}")
        _startTimeLiveData.postValue(cal)
    }
    fun setEndTime(cal: Calendar) {
        Log.e("setEndTime", "time - ${cal.time}")
        _endTimeLiveData.postValue(cal)
    }
    fun setLocation(location: String) {
        _locationLiveData.postValue(location)
    }
    fun setUser(userList: List<String>) {
        _userLiveData.postValue(userList)
    }
    fun setReminder(reminderList: List<InsertReminderData>) {
        _reminderLiveData.postValue(reminderList)
    }
}