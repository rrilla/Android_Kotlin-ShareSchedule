package com.example.share_schedule.insertCalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareViewModel: ViewModel() {

    private var _selectLocationLiveData = MutableLiveData<Map<String,String>>()
    val selectLocationLiveData: LiveData<Map<String, String>> = _selectLocationLiveData

    fun setSelectLocation(place: Map<String, String>) {
        _selectLocationLiveData.postValue(place)
    }

}