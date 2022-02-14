package com.example.share_schedule.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.share_schedule.calendar.myview.MonthAdapter
import com.example.share_schedule.databinding.FragmentCalendarBinding
import com.example.share_schedule.databinding.FragmentMonthBinding
import kotlinx.coroutines.*
import java.util.*

class MonthFragment: Fragment() {

    private val sharedViewModel: ShareViewModel by activityViewModels()
    private val monthViewModel: MonthViewModel by viewModels()

    private var _binding: FragmentMonthBinding?= null
    private val binding get() = _binding!!

    private lateinit var monthAdapter: MonthAdapter

    var pageIndex = 0
    private var monthDate = Calendar.getInstance()

    private lateinit var date: Date

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMonthBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDate()
        initAdapter()
        observeData()
    }

    private fun initDate() {
        pageIndex -= (Int.MAX_VALUE / 2)
        date = monthDate.run {
            add(Calendar.MONTH, pageIndex)
            time
        }

        monthViewModel.fetchData(date)
        MainScope().launch {
            binding.monthRecyclerView.setHasFixedSize(true)
            binding.monthRecyclerView.adapter = monthAdapter
        }
    }

    private fun initAdapter() {
        monthAdapter = MonthAdapter(binding.monthViewLayout, date, FragmentCalendarBinding.inflate(layoutInflater))
    }

    private fun observeData() {
        sharedViewModel.checkCalendarListLiveData.observe(viewLifecycleOwner) {
            monthAdapter.setCheckCalenderList(it)
        }

        monthViewModel.curMonthEventListLiveData.observe(viewLifecycleOwner) {
            monthAdapter.setCurMonthEventList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}