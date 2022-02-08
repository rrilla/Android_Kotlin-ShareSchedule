package com.example.share_schedule.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.share_schedule.MyApplication.Companion.preferenceManager
import com.example.share_schedule.base.BaseViewModel
import com.example.share_schedule.data.CalendarRepository
import com.example.share_schedule.data.db.entity.CalendarEntity
import com.example.share_schedule.data.db.entity.EventEntity
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CalendarViewModel: BaseViewModel() {

    private val calendarRepository = CalendarRepository()

    private var _authStateLiveData = MutableLiveData<UserRecoverableAuthIOException>()
    val authStateLiveData: LiveData<UserRecoverableAuthIOException> = _authStateLiveData

    private var _calendarIdLiveData = MutableLiveData<List<CalendarEntity>>()
    val calendarIdLiveData: LiveData<List<CalendarEntity>> = _calendarIdLiveData

    private var _loadingStateLiveData = MutableLiveData<LoadingState>(LoadingState.UnInitialized)
    val loadingStateLiveData: LiveData<LoadingState> = _loadingStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        setLoadingState(LoadingState.Loading)
        val remoteCalendarList = getRemoteCalendarList()
        saveAtLocalCalendarList(remoteCalendarList)
        val localCalendarList = getLocalCalendarList()
        setCalendarIdList(localCalendarList)

        val remoteEventList = getRemoteEventList(localCalendarList)
        saveAtLocalEventList(remoteEventList)
        setLoadingState(LoadingState.Success)
    }

    fun fetchLocalData(): Job = viewModelScope.launch {
        setLoadingState(LoadingState.Loading)
        val localCalendarList = getLocalCalendarList()
        setCalendarIdList(localCalendarList)
        setLoadingState(LoadingState.Success)
    }

    private suspend fun getRemoteCalendarList(): List<CalendarEntity>? {
        var remoteCalendarList : List<CalendarEntity>? = null
        try {
            remoteCalendarList = calendarRepository.getCalendarList()
        }
        catch (exception: UserRecoverableAuthIOException) {
            setAuthState(exception)
        }
        return remoteCalendarList
    }

    private suspend fun saveAtLocalCalendarList(list: List<CalendarEntity>?) {
        if (list != null) {
            calendarRepository.insertLocalCalendarList(list)
        }
    }

    suspend fun getLocalCalendarList(): List<CalendarEntity> {
        return calendarRepository.getLocalCalendarList()
    }

    private suspend fun getRemoteEventList(localCalendarList: List<CalendarEntity>): List<EventEntity> {
        val remoteEventList = mutableListOf<EventEntity>()
        for(item in localCalendarList) {
            remoteEventList += calendarRepository.getEventList(item.id)
        }
        return remoteEventList
    }

    private suspend fun saveAtLocalEventList(list: List<EventEntity>?) {
        if (list != null) {
            calendarRepository.insertLocalEventList(list)
        }
    }

    fun setCalendarIdList(list: List<CalendarEntity>) {
        _calendarIdLiveData.postValue(list)
    }

    fun setAuthState(ioException: UserRecoverableAuthIOException) {
        _authStateLiveData.postValue(ioException)
    }

    private fun setLoadingState(state: LoadingState) {
        _loadingStateLiveData.postValue(state)
    }

    fun signOut() = viewModelScope.launch {
        preferenceManager.removedIdToken()
        preferenceManager.removeCalendarNextSyncToken()
        preferenceManager.removeEventNextSyncToken()
        calendarRepository.deleteAllCalendar()
        calendarRepository.deleteAllEvent()
    }
}