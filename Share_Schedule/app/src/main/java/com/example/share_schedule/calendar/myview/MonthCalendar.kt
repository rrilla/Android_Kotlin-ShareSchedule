package com.example.share_schedule.calendar.myview

import java.util.*

class MonthCalendar(date: Date) {

    val calendar: Calendar = Calendar.getInstance()
    val nextCalendar: Calendar = Calendar.getInstance()

    var prevTail = 0
    var nextHead = 0
    var currentMaxDate = 0

    var rows = 0

    var dateList = arrayListOf<Int>()

    init {
        calendar.time = date
        nextCalendar.time = date
    }

    fun initBaseCalendar() {
        makeMonthDate()
    }

    private fun makeMonthDate() {
        dateList.clear()

        calendar.set(Calendar.DATE, 1)
        currentMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        prevTail = calendar.get(Calendar.DAY_OF_WEEK) - 1

        nextCalendar.set(Calendar.DATE, currentMaxDate)
        nextHead = 7 - nextCalendar.get(Calendar.DAY_OF_WEEK)

        rows = (prevTail + currentMaxDate + nextHead) / 7

        makePrevTail(calendar.clone() as Calendar)
        makeCurrentMonth(calendar)
        makeNextHead()
    }

    private fun makePrevTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        var maxOffsetDate = maxDate - prevTail

        for (i in 1..prevTail) dateList.add(++maxOffsetDate)
    }

    private fun makeCurrentMonth(calendar: Calendar) {
        for (i in 1..calendar.getActualMaximum(Calendar.DATE))  dateList.add(i)
    }

    private fun makeNextHead() {
        var date = 1
        for (i in 1..nextHead) dateList.add(date++)
    }
}