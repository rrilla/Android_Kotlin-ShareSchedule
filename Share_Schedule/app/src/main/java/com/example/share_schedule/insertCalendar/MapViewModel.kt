package com.example.share_schedule.insertCalendar

import android.telecom.Call
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.launch

class MapViewModel: ViewModel() {
    private var _placeLiveData = MutableLiveData<List<Place>>()
    val placeLiveData: LiveData<List<Place>> = _placeLiveData

    fun searchByProminence(location: LatLng, type: String) = viewModelScope.launch {

    }

    fun searchByDistance(location: LatLng, type: String) = viewModelScope.launch {

    }

    private fun setPlace(list: List<Place>){
        Log.e("MapViewModel","setPlace실행")
        _placeLiveData.postValue(list)
    }
}