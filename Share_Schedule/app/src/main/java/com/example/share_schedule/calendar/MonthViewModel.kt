package com.example.share_schedule.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.share_schedule.data.CalendarRepository
import com.example.share_schedule.data.db.entity.EventEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MonthViewModel: ViewModel() {

    private val calendarRepository = CalendarRepository()

    private var _curMonthEventListLiveData = MutableLiveData<List<EventEntity>>()
    var curMonthEventListLiveData : LiveData<List<EventEntity>> = _curMonthEventListLiveData

    fun fetchData(date: Date): Job = viewModelScope.launch {
        val beforeDate = transformBeforeDate(date)
        val afterDate = transformAfterDate(date)
        val curMonthEventList = getCurrentMonthEvent(beforeDate, afterDate)
        setCurMonthEventList(curMonthEventList)
    }

    private fun transformBeforeDate(curDate: Date): String {
        val beforeCalendar = Calendar.getInstance()
        beforeCalendar.time = curDate
        beforeCalendar.set(Calendar.DATE, 1)

        return formatDate(beforeCalendar.time)
    }

    private fun transformAfterDate(curDate: Date): String {
        val afterCalendar = Calendar.getInstance()
        afterCalendar.time = curDate
        afterCalendar.add(Calendar.MONTH, 1)
        afterCalendar.set(Calendar.DATE, 1)

        return formatDate(afterCalendar.time)
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        return dateFormat.format(date)
    }

    private suspend fun getCurrentMonthEvent(beforeDate: String, afterDate: String): List<EventEntity> {
        return calendarRepository.getLocalEventListWithDate(beforeDate, afterDate)
    }

    private fun setCurMonthEventList(list: List<EventEntity>) {
        _curMonthEventListLiveData.postValue(list)
    }
}