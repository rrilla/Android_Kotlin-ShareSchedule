package com.example.share_schedule.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jh_calendar.signin.ProfileState
import com.example.share_schedule.MyApplication
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel: ViewModel() {
    private val preferenceManager = MyApplication.preferenceManager

    private var _profileStateLiveData = MutableLiveData<ProfileState>(ProfileState.Uninitialized)
    val profileStateLiveData: LiveData<ProfileState> = _profileStateLiveData

    private fun fetchData(): Job = viewModelScope.launch {
        setState(ProfileState.Loading)
        preferenceManager.getIdToken()?.let {
            setState(ProfileState.Login(it))
        } ?: kotlin.run {
            setState(ProfileState.Success.NotRegistered)
        }
    }

    fun setState(state: ProfileState) {
        _profileStateLiveData.postValue(state)
    }

    fun saveToken(idToken: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            preferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch {
        firebaseUser?.let { user->
            setState(
                ProfileState.Success.Registered(
                    user.displayName ?: "익명",
                    user.photoUrl,
                    listOf()
                )
            )
        } ?: kotlin.run {
            setState(ProfileState.Success.NotRegistered)
        }
    }
}