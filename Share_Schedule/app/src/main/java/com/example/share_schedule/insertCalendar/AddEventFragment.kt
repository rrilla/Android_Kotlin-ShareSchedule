package com.example.share_schedule.insertCalendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
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
import com.example.share_schedule.MyApplication
import com.example.share_schedule.R
import com.example.share_schedule.calendar.CalendarActivity
import com.example.share_schedule.data.remote.model.event.Event
import com.example.share_schedule.data.remote.model.event.InsertEventEntity
import com.example.share_schedule.databinding.FragmentAddEventBinding
import com.example.share_schedule.insertCalendar.adapter.ReminderAdapter
import com.example.share_schedule.insertCalendar.adapter.UserAdapter
import com.example.share_schedule.insertCalendar.util.InsertCalendarDialog
import com.example.share_schedule.signin.ProfileState
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.template.model.*
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
            setPositiveButton("??????") { _, _ ->
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
                binding.startTime.text = "${hour} ??? ${minute}???"
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
                binding.endTime.text = "${hour} ??? ${minute}???"
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
            //  ListAdapter??? ????????? ????????? ?????? ?????? ?????????
            val listArr = arrayListOf<String>()
            for (item in it){
                listArr.add(item?.summary ?: "???????????? ?????????" )
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
            binding.startDate.text = "${it.get(Calendar.YEAR)}??? ${it.get(Calendar.MONTH)+1}??? ${it.get(Calendar.DATE)}???"
            binding.startTime.text = "${it.get(Calendar.HOUR_OF_DAY)} ??? ${it.get(Calendar.MINUTE)}???"
        }
        viewModel.endDateTimeLiveData.observe(this){
            binding.endDate.text = "${it.get(Calendar.YEAR)}??? ${it.get(Calendar.MONTH)+1}??? ${it.get(Calendar.DATE)}???"
            binding.endTime.text = "${it.get(Calendar.HOUR_OF_DAY)} ??? ${it.get(Calendar.MINUTE)}???"
        }
        viewModel.insertStateLiveData.observe(this){
            when (it) {
                is InsertState.Loading -> handleLoadingState()
                is InsertState.Success -> handleSuccessState()
                is InsertState.Error -> handleErrorState()
            }
        }

        shareViewModel.selectLocationLiveData.observe(this){
            if(it["title"] == null || it["address"] == null){
                binding.addLocation.text = getString(R.string.error_location)
            } else{
                binding.addLocation.text = it["title"]
            }
        }
    }

    private fun handleLoadingState() { }

    private fun handleSuccessState() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(R.string.confirmShareSchedule)
            setPositiveButton("??????") { _, _ ->
                sendKakaoLink()
                passToCalendarActivity()
            }
            setNegativeButton("??????") { _, _ ->
                passToCalendarActivity()
            }
        }.show()
    }

    private fun handleErrorState() { }

    private fun passToCalendarActivity() {
        with(requireActivity()){
            intent = Intent(context, CalendarActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(getString(R.string.autoLogin), false)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun sendKakaoLink() {
        val defaultFeed = FeedTemplate(
            content = Content(
                title = "'${MyApplication.firebaseAuth.currentUser?.displayName}'?????? ????????? ?????? ???????????????.",
                imageUrl = "https://post-phinf.pstatic.net/MjAyMDA2MDRfMjcz/MDAxNTkxMjMyNDIwODAy.Zb1gf9wnPBXyh2iwqt6WbG9NVlwKAbA0aZb3VCtLS28g.PZ22F-FY0uq_7snQ3i_VNcmBLDWkZA4Vv1wE_MsxBBcg.JPEG/fsdfsdf.JPG.jpg",
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            )
        )

        // ???????????? ???????????? ??????
        if (LinkClient.instance.isKakaoLinkAvailable(requireContext())) {
            val TAG = "AddEventFragment"
            // ?????????????????? ??????????????? ?????? ??????
            LinkClient.instance.defaultTemplate(requireContext(), defaultFeed) { linkResult, error ->
                if (error != null) {
                    Log.e(TAG, "??????????????? ????????? ??????", error)
                }
                else if (linkResult != null) {
                    Log.d(TAG, "??????????????? ????????? ?????? ${linkResult.intent}")
                    startActivity(linkResult.intent)

                    // ??????????????? ???????????? ??????????????? ?????? ?????? ???????????? ????????? ?????? ?????? ???????????? ?????? ???????????? ?????? ??? ????????????.
                    Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                    Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                }
            }
        } else {
            // ???????????? ?????????: ??? ?????? ?????? ??????
            val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultFeed)

            // 1. CustomTabs?????? Chrome ???????????? ??????
            try {
                KakaoCustomTabsClient.openWithDefault(requireContext(), sharerUrl)
            } catch(e: UnsupportedOperationException) {
                // Chrome ??????????????? ?????? ??? ????????????
            }
            // 2. CustomTabs?????? ???????????? ?????? ???????????? ??????
            try {
                KakaoCustomTabsClient.open(requireContext(), sharerUrl)
            } catch (e: ActivityNotFoundException) {
                // ????????? ??????????????? ?????? ??? ????????????
            }
        }
    }
}