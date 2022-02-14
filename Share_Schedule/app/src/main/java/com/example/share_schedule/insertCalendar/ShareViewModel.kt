package com.example.share_schedule.insertCalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.share_schedule.data.Test

class ShareViewModel: ViewModel() {

    val test = Test()
    private var _selectLocationLiveData = MutableLiveData<Map<String, String>>()
    val selectLocationLiveData: LiveData<Map<String, String>> = _selectLocationLiveData

    fun setSelectLocation(place: Map<String, String>) {
        _selectLocationLiveData.postValue(place)
    }

}