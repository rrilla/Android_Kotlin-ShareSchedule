package com.example.share_schedule.insertCalendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.share_schedule.R
import com.example.share_schedule.databinding.FragmentAddEventBinding
import com.example.share_schedule.insertCalendar.adapter.InsertCalendarData
import com.example.share_schedule.insertCalendar.adapter.ReminderAdapter
import com.example.share_schedule.insertCalendar.adapter.UserAdapter
import com.example.share_schedule.insertCalendar.util.InsertCalendarDialog
import java.util.*

class AddEventFragment : Fragment() {

    lateinit var binding: FragmentAddEventBinding
    private val viewModel: AddEventViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()

    val userAdapter: UserAdapter by lazy {
        UserAdapter()
    }
    val reminderAdapter: ReminderAdapter by lazy {
        ReminderAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEventBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeData()
    }

    private fun initViews() {
        binding.recycleViewUser.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleViewUser.adapter = userAdapter
        binding.recycleViewReminder.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleViewReminder.adapter = reminderAdapter

        binding.finishButton.setOnClickListener {
            termination()
        }

        binding.startDate.setOnClickListener {
            selectDate("start")
        }

        binding.startTime.setOnClickListener {
            selectTime("start")
        }

        binding.endDate.setOnClickListener {
            selectDate("end")
        }

        binding.endTime.setOnClickListener {
            selectTime("end")
        }

        binding.addLocation.setOnClickListener {
            (requireActivity() as InsertActivity).replaceFragment(MapFragment())
        }

        binding.addAttendees.setOnClickListener {
            //  custom dialog
            InsertCalendarDialog(InsertCalendarDialog.MyDialogTag.USER,
                object: InsertCalendarDialog.InsertCalendarDialogListener{
                    override fun onDialogPositiveClick(dialog: DialogFragment, data: InsertCalendarData) {
                        userAdapter.addItem(data.email)
                    }
                }).show(parentFragmentManager, "dialogUser")
        }

        binding.addReminder.setOnClickListener {
            InsertCalendarDialog(InsertCalendarDialog.MyDialogTag.REMINDER,
                object: InsertCalendarDialog.InsertCalendarDialogListener{
                    override fun onDialogPositiveClick(dialog: DialogFragment, data: InsertCalendarData) {
                        reminderAdapter.addItem(data)
                    }
                }).show(parentFragmentManager, "dialogReminder")
        }

        binding.saveButton.setOnClickListener {
            val summary = binding.addSummary.text.toString()
            val description = binding.addDescription.text.toString()
            viewModel.setUser(userAdapter.data)
            viewModel.setReminder(reminderAdapter.data)
            shareViewModel.selectLocationLiveData
        }
    }

    private fun termination() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(getString(R.string.confirmInsertCalendar))
            setPositiveButton("삭제") { _, _ ->
                requireActivity().finish()
            }
        }.create().show()
    }

    private fun selectDate(type: String) {
        val cal = Calendar.getInstance()
        if(type == "start"){
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)
                viewModel.setStartDate(cal)
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
                Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
        } else{
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)
                viewModel.setEndDate(cal)
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
                Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
        }
    }

    private fun selectTime(type: String) {
        val cal = Calendar.getInstance()
        if(type == "start"){
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                binding.startTime.text = "${hour} 시 ${minute}분"
                cal.set(Calendar.HOUR, hour)
                cal.set(Calendar.MINUTE, minute)
                viewModel.setStartTime(cal)
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE), true)
                .show()
        } else{
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                binding.endTime.text = "${hour} 시 ${minute}분"
                cal.set(Calendar.HOUR, hour)
                cal.set(Calendar.MINUTE, minute)
                viewModel.setEndTime(cal)
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE), true)
                .show()
        }
    }

    private fun observeData() {
        viewModel.startDateLiveData.observe(this){
            binding.startDate.text = "${it.get(Calendar.YEAR)}년 ${it.get(Calendar.MONTH)+1}월 ${it.get(Calendar.DATE)}일"
        }
        viewModel.endDateLiveData.observe(this){
            binding.endDate.text = "${it.get(Calendar.YEAR)}년 ${it.get(Calendar.MONTH)+1}월 ${it.get(Calendar.DATE)}일"
        }
        viewModel.startTimeLiveData.observe(this){
            binding.startTime.text = "${it.get(Calendar.HOUR_OF_DAY)} 시 ${it.get(Calendar.MINUTE)}분"
        }
        viewModel.endTimeLiveData.observe(this){
            binding.endTime.text = "${it.get(Calendar.HOUR_OF_DAY)} 시 ${it.get(Calendar.MINUTE)}분"
        }


        shareViewModel.selectLocationLiveData.observe(this){
            if(it["title"] == null || it["address"] == null){
                binding.addLocation.text = getString(R.string.error_location)
            } else{
                binding.addLocation.text = it["title"]
                viewModel.setLocation(it["address"]!!)
            }
        }
    }


}