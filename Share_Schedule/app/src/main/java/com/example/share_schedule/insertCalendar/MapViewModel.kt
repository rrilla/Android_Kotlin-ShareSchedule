package com.example.share_schedule.insertCalendar

import android.util.Log
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
            BuildConfig.GOOGLE_API_KEY,
            "${location.latitude}, ${location.longitude}",
            type)
            .enqueue(object: Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    Log.e("MapViewModel","onResponse - ${response.body()?.results.toString()}")
                    setPlace(response.body()?.results!!)
                }
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Log.e("MapViewModel","onFailure - ${t.printStackTrace()}")
                }
            })
    }

    fun searchByDistance(location: LatLng, type: String) = viewModelScope.launch {
        GooglePlaceApiProvider.googlePlaceApiService.searchByDistance(
            BuildConfig.GOOGLE_API_KEY,
            "${location.latitude}, ${location.longitude}",
            type)
            .enqueue(object: Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    Log.e("MapViewModel","onResponse - ${response.body()?.results.toString()}")
                    setPlace(response.body()?.results!!)
                }
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Log.e("MapViewModel","onFailure - ${t.printStackTrace()}")
                }
            })
    }

    private fun setPlace(list: List<Place>){
        _placeLiveData.postValue(list)
    }
}