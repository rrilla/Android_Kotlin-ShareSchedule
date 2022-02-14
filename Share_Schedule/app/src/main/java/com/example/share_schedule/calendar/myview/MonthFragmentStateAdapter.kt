package com.example.share_schedule.calendar.myview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.share_schedule.calendar.MonthFragment

class MonthFragmentStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    val monthFragmentPosition = Int.MAX_VALUE / 2
    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val monthFragment = MonthFragment()
        monthFragment.pageIndex = position
        return monthFragment
    }
}