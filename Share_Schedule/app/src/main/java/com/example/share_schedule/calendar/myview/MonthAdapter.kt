package com.example.share_schedule.calendar.myview

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.*

import com.example.share_schedule.data.db.entity.EventEntity
import com.example.share_schedule.databinding.FragmentCalendarBinding
import com.example.share_schedule.databinding.ItemMonthviewBinding

import java.text.SimpleDateFormat
import java.util.*

class MonthAdapter(
    val calendarLayout: LinearLayout,
    private val today: Date,
    val parentBinding: FragmentCalendarBinding
) : RecyclerView.Adapter<MonthAdapter.MonthItemHolder>() {

    var dateList: ArrayList<Int> = arrayListOf()
    var monthCalendar = MonthCalendar(today)
    var rows = 0
    var maxPos = 0

    val curCalendar = Calendar.getInstance()
    val curYear = curCalendar.get(Calendar.YEAR)
    val curMonth = curCalendar.get(Calendar.MONTH)

    private var curMonthEventList: List<EventEntity> = listOf()
    private var checkCalendarList: List<String> = listOf()

    private var hasEventPositionSet: MutableSet<Int> = mutableSetOf()
    private var connectedEventSet: MutableSet<EventEntity> = mutableSetOf()

    init {
        monthCalendar.initBaseCalendar()
        dateList = monthCalendar.dateList
        rows = monthCalendar.rows
        maxPos = rows * 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthItemHolder {
        val binding =
            ItemMonthviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonthItemHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthItemHolder, position: Int) {
        holder.bind(dateList[position], position)
    }

    override fun getItemCount(): Int = dateList.size

    companion object {
        val koreanHolidayId = "ko.south_korea#holiday@group.v.calendar.google.com"
        val koreanHoliday = listOf<String>("광복절", "신정", "설날", "설날 연휴", "삼일절", "어린이날", "석가탄신일", "현충일", "광복절", "추석", "추석 연휴", "개천절", "한글날", "크리스마스")
        val tmpHoliday = "쉬는 날"
    }

    inner class MonthItemHolder(private val binding: ItemMonthviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Int, position: Int) {
            val h = calendarLayout.height / rows
            binding.root.layoutParams.height = h

            val firstDateIndex = monthCalendar.prevTail
            val lastDateIndex = dateList.size - monthCalendar.nextHead - 1

            binding.dateNumberTextView.text = date.toString()

            val dateString = SimpleDateFormat("dd", Locale.KOREA).format(today)
            val dateInt = dateString.toInt()

            val monthString = SimpleDateFormat("MM", Locale.KOREA).format(today)
            val monthInt = monthString.toInt()

            val yearString = SimpleDateFormat("yyyy", Locale.KOREA).format(today)
            val yearInt = yearString.toInt()

            val curItemDate = Calendar.getInstance()
            curItemDate.time = today
            curItemDate.set(Calendar.DATE, date)

            val curDateString = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(curItemDate.time)

            if (date == dateInt && curMonth == monthInt - 1 && curYear == yearInt) {
                binding.dateNumberTextView.setTypeface(
                    binding.dateNumberTextView.typeface,
                    Typeface.BOLD
                )
            }
            if (position < firstDateIndex) {
                binding.root.alpha = 0.5f
                curItemDate.add(Calendar.MONTH, -1)
            } else if (position > lastDateIndex) {
                binding.root.alpha = 0.5f
                curItemDate.add(Calendar.MONTH, 1)
            } else {
                binding.root.alpha = 1.0f
                binding.eventLayout.removeAllViewsInLayout()
                if (position % 7 == 0) {
                    binding.dateNumberTextView.setTextColor(Color.RED)
                } else {
                    binding.dateNumberTextView.setTextColor(Color.BLACK)
                }

                curItemDate.add(Calendar.DATE, 1)
                val afterDate = curItemDate.time
                val afterDateString = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(afterDate)

                for (event in curMonthEventList) {
                    if (!checkCalendarList.contains(event.calendarId)) continue

                    val eventStartDate = event.start?.date
                    val eventEndDate = event.end?.endDate
                    val eventStartDateTime = event.start?.dateTime
                    val eventEndDateTime = event.end?.endDateTime

                    if (eventStartDate != null && eventEndDate != null) {
                        // 1일 date
                        if (eventStartDate == curDateString && eventEndDate == afterDateString) {
                            hasEventPositionSet.add(position)
                            addTextView(binding, event, 0)

                            // 공휴일 처리
                            if(event.calendarId == koreanHolidayId) {
                                if(event.summary in koreanHoliday || event.summary?.contains(
                                        tmpHoliday
                                    ) == true) {
                                    binding.dateNumberTextView.setTextColor(Color.RED)
                                }
                            }
                        }

                        // 여러 일 date
                        else if (eventStartDate <= curDateString && eventEndDate > curDateString) {
                            hasEventPositionSet.add(position)
                            if(event in connectedEventSet) {
                                addConnectedTextView(binding, event, 0)
                            }

                            else {
                                connectedEventSet.add(event)
                                addConnectedTextView(binding, event, 1)
                            }
                        }

                    }

                    if (eventStartDateTime != null && eventEndDateTime != null) {
                        val startDateTimeToDate = eventStartDateTime.substring(0, 10)
                        val endDateTimeToDate = eventEndDateTime.substring(0, 10)

                        // 1일
                        if (startDateTimeToDate == curDateString && endDateTimeToDate == curDateString) {
                            hasEventPositionSet.add(position)
                            addTextView(binding, event, 0)
                        }

                        // 여러 일
                        else if (eventStartDateTime <= afterDateString && eventEndDateTime >= curDateString) {
                            hasEventPositionSet.add(position)
                            if(event in connectedEventSet) {
                                addConnectedTextView(binding, event, 0)
                            }
                            else {
                                connectedEventSet.add(event)
                                addConnectedTextView(binding, event, 1)
                            }
                        }
                    }

                }
            }
        }
    }

    fun setCheckCalenderList(list: List<String>) {
        checkCalendarList = list
        connectedEventSet.clear()
        for (i in 0 until maxPos) {
            if (i in hasEventPositionSet) {
                notifyItemChanged(i)
            }
        }
    }

    fun setCurMonthEventList(list: List<EventEntity>) {
        curMonthEventList = list
        notifyDataSetChanged()
    }

    fun addTextView(binding: ItemMonthviewBinding, event: EventEntity, flag: Int) {
        val textView = TextView(binding.root.context)
        textView.text = event.summary
        textView.textSize = 12F
        if (event.creator?.creatorDisplayName == "대한민국의 휴일") textView.setBackgroundColor(Color.RED)
        else textView.setBackgroundColor(Color.BLUE)
        textView.setTextColor(Color.WHITE)

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (flag == 0) params.setMargins(0, 0, 3, 3)
        else params.setMargins(0, 0, 0, 3)
        textView.layoutParams = params
        textView.maxLines = 1
        binding.eventLayout.addView(textView)
    }

    fun addConnectedTextView(binding: ItemMonthviewBinding, event: EventEntity, flag: Int) {
        val textView = TextView(binding.root.context)
        if (flag == 1) {
            textView.text = event.summary
            textView.textSize = 12F
        } else {
            textView.text = event.summary
            textView.textSize = 12F
        }
        if (event.creator?.creatorDisplayName == "대한민국의 휴일") textView.setBackgroundColor(Color.RED)
        else textView.setBackgroundColor(Color.GRAY)
        textView.setTextColor(Color.WHITE)

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 3)
        textView.layoutParams = params
        textView.maxLines = 1

        binding.eventLayout.addView(textView)
    }
}