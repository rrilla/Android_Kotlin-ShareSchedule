package com.example.share_schedule.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ShareViewModel: ViewModel() {

    private var _checkCalendarListLiveData = MutableLiveData<List<String>>()
    var checkCalendarListLiveData : LiveData<List<String>> = _checkCalendarListLiveData

    var year = 0
    var month = 0

    init {
        val calendar= Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
    }

    fun getPosition(selectedCalendar: Calendar, currentCalendar: Calendar): Int {
        return ((selectedCalendar.get(Calendar.YEAR)-currentCalendar.get(Calendar.YEAR))*12 + (selectedCalendar.get(Calendar.MONTH) - currentCalendar.get(Calendar.MONTH)))
    }

    fun setCheckCalendarList(list: List<String>) {
        _checkCalendarListLiveData.postValue(list)
    }
}