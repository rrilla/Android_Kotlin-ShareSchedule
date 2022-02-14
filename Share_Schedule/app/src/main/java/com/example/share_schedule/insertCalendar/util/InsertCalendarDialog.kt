package com.example.share_schedule.insertCalendar.util

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.share_schedule.R
import com.example.share_schedule.databinding.DialogInsertBinding
import com.example.share_schedule.insertCalendar.adapter.InsertCalendarData

class InsertCalendarDialog(private val TAG: MyDialogTag, private val listener: InsertCalendarDialogListener): DialogFragment() {

    sealed class MyDialogTag {
        object CALENDAR: MyDialogTag()
        object USER: MyDialogTag()
        object REMINDER: MyDialogTag()
    }

    interface InsertCalendarDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, data: InsertCalendarData)
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

                }
                MyDialogTag.USER -> {
                    builder.setTitle(R.string.addUserDialogTitle)
                    binding.reminderLayout.visibility = View.GONE
                    binding.okButton.setOnClickListener {
                        val email = binding.emailEditText.text.toString().trim()
                        if(checkEmail(email)){
                            listener.onDialogPositiveClick(this, InsertCalendarData(email))
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
                            listener.onDialogPositiveClick(
                                this,
                                InsertCalendarData("", time.toInt(), timeRadioButtonValue, typeRadioButtonValue)
                            )
                            dismiss()
                        }else{
                            binding.checkTimeTextView.text = getString(R.string.errorTime)
                        }
                    }
                }
            }
//            binding.addButton.setOnClickListener {
//                val data = binding.editTextTextPersonName3.text.toString()
//                listener.onDialogPositiveClick(this, data)
//                dialog?.cancel()
//            }
//            binding.cancelButton.setOnClickListener {
//                dialog?.cancel()
//            }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun checkEmail(email: String): Boolean {
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}