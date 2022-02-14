package com.example.share_schedule.insertCalendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.share_schedule.R
import com.example.share_schedule.data.remote.model.event.Event
import com.example.share_schedule.data.remote.model.event.InsertEventEntity
import com.example.share_schedule.databinding.FragmentAddEventBinding
import com.example.share_schedule.insertCalendar.adapter.ReminderAdapter
import com.example.share_schedule.insertCalendar.adapter.UserAdapter
import com.example.share_schedule.insertCalendar.util.InsertCalendarDialog
import java.util.*

class AddEventFragment : Fragment() {

    lateinit var binding: FragmentAddEventBinding
    private val viewModel: AddEventViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()

    private val userAdapter: UserAdapter by lazy {
        UserAdapter()
    }
    private val reminderAdapter: ReminderAdapter by lazy {
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

        viewModel.getCalendarList()
        viewModel.setStartDateTime(Calendar.getInstance())
        viewModel.setEndDateTime(Calendar.getInstance().apply { set(Calendar.DATE, this.get(Calendar.DATE)+1) })
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
            InsertCalendarDialog(InsertCalendarDialog.MyDialogTag.USER).apply {
                setUserOnClickListener {
                    dialogFragment, email -> userAdapter.addItem(email)
                }
            }.show(parentFragmentManager, "dialogUser")
        }

        binding.addReminder.setOnClickListener {
            InsertCalendarDialog(InsertCalendarDialog.MyDialogTag.REMINDER).apply {
                setReminderOnClickListener {
                    dialogFragment, data -> reminderAdapter.addItem(data)
                }
            }.show(parentFragmentManager, "dialogReminder")
        }

        binding.saveButton.setOnClickListener {
            val summary = binding.addSummary.text.toString()
            val description = binding.addDescription.text.toString()
            val event = InsertEventEntity().apply {
                this.summary = summary
                this.description = description
                this.location = shareViewModel.selectLocationLiveData.value?.get("address")
                this.attendees = userAdapter.data
                this.reminders = reminderAdapter.data
            }
            viewModel.insertEvent(event)
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

        if(type == "start"){
            val cal = viewModel.startDateTimeLiveData.value!!
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)
                viewModel.setStartDateTime(cal)
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
                Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
        } else{
            val cal = viewModel.endDateTimeLiveData.value!!
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)
                viewModel.setEndDateTime(cal)
            }
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
                Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
        }
    }

    private fun selectTime(type: String) {
        if(type == "start"){
            val cal = viewModel.startDateTimeLiveData.value!!
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                binding.startTime.text = "${hour} 시 ${minute}분"
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                Log.e("selectTime", """
                    displayname - ${cal.timeZone.displayName}
                    id - ${cal.timeZone.id}
                    string - ${cal.timeZone.toString()}""".trimIndent())
                cal.time
                viewModel.setStartDateTime(cal)
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE), true)
                .show()
        } else{
            val cal = viewModel.endDateTimeLiveData.value!!
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                binding.endTime.text = "${hour} 시 ${minute}분"
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                viewModel.setEndDateTime(cal)
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE), true)
                .show()
        }
    }

    private fun observeData() {
        viewModel.calendarListLiveData.observe(this){
            viewModel.setSelectCalendar(0)
            //  ListAdapter로 캘린더 목록에 따라 동적 바인딩
            val listArr = arrayListOf<String>()
            for (item in it){
                listArr.add(item?.summary ?: "제목없는 캘린더" )
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listArr)

            binding.addCalendarTextView.setOnClickListener {
                InsertCalendarDialog(InsertCalendarDialog.MyDialogTag.CALENDAR).apply {
                    setCalendarOnClickListener{ dialogFragment, position ->
                        viewModel.setSelectCalendar(position)
                    }
                    setAdapter(adapter)
                }.show(parentFragmentManager, "dialogCalendar")
            }
        }
        viewModel.selectCalendarLiveData.observe(this){
            binding.addCalendarTextView.text = it.summary
        }
        viewModel.startDateTimeLiveData.observe(this){
            binding.startDate.text = "${it.get(Calendar.YEAR)}년 ${it.get(Calendar.MONTH)+1}월 ${it.get(Calendar.DATE)}일"
            binding.startTime.text = "${it.get(Calendar.HOUR_OF_DAY)} 시 ${it.get(Calendar.MINUTE)}분"
        }
        viewModel.endDateTimeLiveData.observe(this){
            binding.endDate.text = "${it.get(Calendar.YEAR)}년 ${it.get(Calendar.MONTH)+1}월 ${it.get(Calendar.DATE)}일"
            binding.endTime.text = "${it.get(Calendar.HOUR_OF_DAY)} 시 ${it.get(Calendar.MINUTE)}분"
        }

        shareViewModel.selectLocationLiveData.observe(this){
            if(it["title"] == null || it["address"] == null){
                binding.addLocation.text = getString(R.string.error_location)
            } else{
                binding.addLocation.text = it["title"]
            }
        }
    }
}