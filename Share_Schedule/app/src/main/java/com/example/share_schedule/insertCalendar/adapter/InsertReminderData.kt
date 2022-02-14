package com.example.share_schedule.insertCalendar.adapter

data class InsertReminderData (
    val time: Int = 10,
    val timeType: String = "",
    val reminderType: String = ""
) {
    fun getTimeOfNumber(): Int {
        return when(timeType) {
            "분 전" -> time
            "시간 전" -> time * 60
            "일 전" -> time * 60 * 24
            else -> 0
        }
    }
}
