package com.example.share_schedule.signin

import android.net.Uri
import com.google.api.services.calendar.model.CalendarListEntry

sealed class ProfileState {
    object Uninitialized: ProfileState()

    object Loading: ProfileState()

    data class Login(
        val idToken: String
    ): ProfileState()

    sealed class Success: ProfileState() {
        data class Registered(
            val userName: String,
            val profileImageUri: Uri?,
            val calendarList: List<CalendarListEntry> = listOf()
        ): Success()

        object NotRegistered: Success()
    }

    object Error: ProfileState()
}