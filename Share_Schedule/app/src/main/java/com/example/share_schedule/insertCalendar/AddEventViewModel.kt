package com.example.share_schedule.insertCalendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.share_schedule.insertCalendar.adapter.InsertCalendarData
import java.text.SimpleDateFormat
import java.util.*

class AddEventViewModel: ViewModel() {

    private var _summaryLiveData = MutableLiveData<String>()
    val summaryLiveData: LiveData<String> = _summaryLiveData

    private var _calendarLiveData = MutableLiveData<Calendar>()
    val calendarLiveData: LiveData<Calendar> = _calendarLiveData

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

    private var _reminderLiveData = MutableLiveData<List<InsertCalendarData>>()
    val reminderLiveData: LiveData<List<InsertCalendarData>> = _reminderLiveData

    private var _descriptionLiveData = MutableLiveData<String>()
    val descriptionLiveData: LiveData<String> = _descriptionLiveData

    //  Google Calendar에서 요구하는 RFC3339형식으로 변경
    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'")
        return dateFormat.format(date)
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
    fun setReminder(reminderList: List<InsertCalendarData>) {
        _reminderLiveData.postValue(reminderList)
    }
}