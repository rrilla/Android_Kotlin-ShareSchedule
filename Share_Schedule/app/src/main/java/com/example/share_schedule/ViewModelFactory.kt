package com.example.share_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.share_schedule.calendar.CalendarViewModel
import com.example.share_schedule.calendar.ShareViewModel
import com.example.share_schedule.signin.LoginViewModel

abstract class ViewModelFactory (private val preferenceManager: PreferenceManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) = with(modelClass) {
         when {
            isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel()
             isAssignableFrom(CalendarViewModel::class.java) ->
                 CalendarViewModel()
//             isAssignableFrom(ShareViewModel::class.java) ->
//                 ShareViewModel()
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
//        return modelClass.getConstructor(LoginViewModel::class.java).newInstance(preferenceManager)
    } as T
}