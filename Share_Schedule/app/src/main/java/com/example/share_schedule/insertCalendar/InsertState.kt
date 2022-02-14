package com.example.share_schedule.insertCalendar

sealed class InsertState {
    object Loading: InsertState()
    object Success: InsertState()
    object Error: InsertState()
}