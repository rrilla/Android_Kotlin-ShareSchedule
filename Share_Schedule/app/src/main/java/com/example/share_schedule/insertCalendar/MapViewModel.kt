package com.example.share_schedule.insertCalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.share_schedule.BuildConfig
import com.example.share_schedule.data.remote.GooglePlaceApiProvider
import com.example.share_schedule.data.remote.model.map.Place
import com.example.share_schedule.data.remote.model.map.ResponseModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel: ViewModel() {
    private var _placeLiveData = MutableLiveData<List<Place>>()
    val placeLiveData: LiveData<List<Place>> = _placeLiveData

    fun searchByProminence(location: LatLng, type: String) = viewModelScope.launch {
        GooglePlaceApiProvider.googlePlaceApiService.searchByProminence(
            BuildConfig.GOOGLE_API_KEY2,
            "${location.latitude}, ${location.longitude}",
            type)
            .enqueue(object: Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    setPlace(response.body()?.results!!)
                }
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    fun searchByDistance(location: LatLng, type: String) = viewModelScope.launch {
        GooglePlaceApiProvider.googlePlaceApiService.searchByDistance(
            BuildConfig.GOOGLE_API_KEY2,
            "${location.latitude}, ${location.longitude}",
            type)
            .enqueue(object: Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    setPlace(response.body()?.results!!)
                }
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun setPlace(list: List<Place>){
        _placeLiveData.postValue(list)
    }
}