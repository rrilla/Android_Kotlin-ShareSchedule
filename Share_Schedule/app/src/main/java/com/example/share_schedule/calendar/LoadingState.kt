package com.example.share_schedule.calendar

sealed class LoadingState {
    object UnInitialized: LoadingState()

    object Loading: LoadingState()

    object Success: LoadingState()
}