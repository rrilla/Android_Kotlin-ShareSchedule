package com.example.share_schedule.insertCalendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.share_schedule.databinding.ItemInsertcalendarBinding

class ReminderAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var data: MutableList<InsertReminderData> = mutableListOf()

    fun addItem(data: InsertReminderData) {
        this.data.add(data)
        notifyDataSetChanged()
    }
    //항목 갯수를 판단하기 위해서 자동 호출
    override fun getItemCount(): Int{
        return data.size
    }
    //항목의 뷰를 가지는 Holder 를 준비하기 위해 자동 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = InsertCalendarItemHolder(
        ItemInsertcalendarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )
    //각 항목을 구성하기 위해서 호출
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as InsertCalendarItemHolder).binding
        //뷰에 데이터 출력
        binding.itemName.text = "${data[position].time} ${data[position].timeType}"
        binding.itemCancel.setOnClickListener {
            data.removeAt(position)
            notifyDataSetChanged()
        }
    }

    inner class InsertCalendarItemHolder(val binding: ItemInsertcalendarBinding) :
        RecyclerView.ViewHolder(binding.root)
}