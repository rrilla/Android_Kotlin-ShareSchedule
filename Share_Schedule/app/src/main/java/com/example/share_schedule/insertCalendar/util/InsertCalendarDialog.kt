package com.example.share_schedule.insertCalendar.util

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ListAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.share_schedule.R
import com.example.share_schedule.databinding.DialogInsertBinding
import com.example.share_schedule.insertCalendar.adapter.InsertReminderData

class InsertCalendarDialog(private val TAG: MyDialogTag): DialogFragment() {

    private lateinit var calendarOnClickListener: CalendarOnClickListener
    private lateinit var userOnClickListener: UserOnClickListener
    private lateinit var reminderOnClickListener: ReminderOnClickListener
    private lateinit var listAdapter: ListAdapter

    sealed class MyDialogTag {
        object CALENDAR: MyDialogTag()
        object USER: MyDialogTag()
        object REMINDER: MyDialogTag()
    }

    interface CalendarOnClickListener {
        fun onCalendarItemClick(dialog: DialogFragment, position: Int)
    }

    interface UserOnClickListener {
        fun onPositiveClick(dialog: DialogFragment, email: String)
    }

    interface ReminderOnClickListener {
        fun onPositiveClick(dialog: DialogFragment, data: InsertReminderData)
    }

    fun setCalendarOnClickListener(listener: (DialogFragment, Int) -> Unit) {
        this.calendarOnClickListener = object: CalendarOnClickListener {
            override fun onCalendarItemClick(dialog: DialogFragment, position: Int) {
                listener(dialog, position)
            }
        }
    }

    fun setUserOnClickListener(listener: (DialogFragment, String) -> Unit) {
        this.userOnClickListener = object: UserOnClickListener {
            override fun onPositiveClick(dialog: DialogFragment, email: String) {
                listener(dialog, email)
            }
        }
    }

    fun setReminderOnClickListener(listener: (DialogFragment, InsertReminderData) -> Unit) {
        this.reminderOnClickListener = object: ReminderOnClickListener {
            override fun onPositiveClick(dialog: DialogFragment, data: InsertReminderData) {
                listener(dialog, data)
            }
        }
    }

    fun setAdapter(adapter: ListAdapter) {
        this.listAdapter = adapter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = DialogInsertBinding.inflate(inflater)
            builder.setView(binding.root)
            when(TAG){
                MyDialogTag.CALENDAR -> {
                    builder.setTitle(R.string.addCalendarDialogTitle)
                    binding.userLayout.visibility = View.GONE
                    binding.reminderLayout.visibility = View.GONE
                    binding.okButton.visibility = View.GONE
                    builder.setAdapter(listAdapter) { dialogInterface, i ->
                        calendarOnClickListener.onCalendarItemClick(this, i)
                    }
                }
                MyDialogTag.USER -> {
                    builder.setTitle(R.string.addUserDialogTitle)
                    binding.reminderLayout.visibility = View.GONE
                    binding.okButton.setOnClickListener {
                        val email = binding.emailEditText.text.toString().trim()
                        if(checkEmail(email)){
                            userOnClickListener.onPositiveClick(this, email)
                            dismiss()
                        }else{
                            binding.checkEmailTextView.text = getString(R.string.errorEmail)
                        }
                    }
                }
                MyDialogTag.REMINDER -> {
                    builder.setTitle(R.string.addReminderDialogTitle)
                    binding.userLayout.visibility = View.GONE
                    binding.okButton.setOnClickListener {
                        val timeRadioGroup = binding.timeRadioGroup.checkedRadioButtonId
                        val typeRadioGroup = binding.typeRadioGroup.checkedRadioButtonId
                        val timeRadioButtonValue = binding.root.findViewById<RadioButton>(timeRadioGroup).text.toString()
                        val typeRadioButtonValue = binding.root.findViewById<RadioButton>(typeRadioGroup).text.toString()
                        val time = binding.timeEditText.text?.toString()

                        if(time != "" && time != null){
                            reminderOnClickListener.onPositiveClick(
                                this,
                                InsertReminderData(time.toInt(), timeRadioButtonValue, typeRadioButtonValue)
                            )
                            dismiss()
                        }else{
                            binding.checkTimeTextView.text = getString(R.string.errorTime)
                        }
                    }
                }
            }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun checkEmail(email: String): Boolean {
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}