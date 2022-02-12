package com.example.share_schedule.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.share_schedule.R
import com.example.share_schedule.databinding.ActivityCalendarBinding
import com.example.share_schedule.insertCalendar.InsertCalendar

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding

    private val autoLogin: Boolean by lazy {
        intent.getBooleanExtra(getString(R.string.autoLogin), false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews(): Unit = with(binding) {
        binding.insertCalendarButton.setOnClickListener {
            intent = Intent(this@CalendarActivity, InsertCalendar::class.java)
            startActivity(intent)
        }
        setTransaction()
    }

    private fun setTransaction() {
        val bundle = Bundle()
        bundle.putBoolean(getString(R.string.autoLogin), autoLogin)

        val calendarFragment = CalendarFragment()
        calendarFragment.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()
            .replace(binding.calendarContainerView.id, calendarFragment)
        transaction.commit()
    }
}