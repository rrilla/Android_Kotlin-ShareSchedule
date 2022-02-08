package com.example.share_schedule.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jh_calendar.calendar.CalendarFragment
import com.example.share_schedule.R
import com.example.share_schedule.databinding.ActivityCalendarBinding

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding
    private fun getViewBinding(): ActivityCalendarBinding = ActivityCalendarBinding.inflate(layoutInflater)

    private val autoLogin: Boolean by lazy {
        intent.getBooleanExtra(getString(R.string.autoLogin), false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        initViews()
    }

    private fun initViews(): Unit = with(binding) {
        setTransaction()
    }

    private fun setTransaction() {
    }
}