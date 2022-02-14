package com.example.share_schedule.insertCalendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
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
import com.example.share_schedule.signin.ProfileState
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.LocationTemplate
import com.kakao.sdk.template.model.Social
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
            setPositiveButton("확인") { _, _ ->
                sendKakaoLink()
            }
            setNegativeButton("취소") { _, _ ->
                requireActivity().finish()
            }
        }.show()
    }

    private fun handleErrorState() { }

    private fun sendKakaoLink() {
        val defaultLocation = LocationTemplate(
            address = "경기 성남시 분당구 판교역로 235 에이치스퀘어 N동 8층",
            addressTitle = "카카오 판교오피스 카페톡",
            content = Content(
                title = "신메뉴 출시❤️ 체리블라썸라떼",
                description = "이번 주는 체리블라썸라떼 1+1",
                imageUrl = "http://mud-kage.kakao.co.kr/dn/bSbH9w/btqgegaEDfW/vD9KKV0hEintg6bZT4v4WK/kakaolink40_original.png",
                link = Link(
                    webUrl = "https://developers.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            )
        )

        // 카카오톡 설치여부 확인
        if (LinkClient.instance.isKakaoLinkAvailable(requireContext())) {
            val TAG = "AddEventFragment"
            // 카카오톡으로 카카오링크 공유 가능
            LinkClient.instance.defaultTemplate(requireContext(), defaultLocation) { linkResult, error ->
                if (error != null) {
                    Log.e(TAG, "카카오링크 보내기 실패", error)
                }
                else if (linkResult != null) {
                    Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}")
                    startActivity(linkResult.intent)

                    // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                    Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                    Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                }
            }
        } else {
            // 카카오톡 미설치: 웹 공유 사용 권장
            val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultLocation)

            // 1. CustomTabs으로 Chrome 브라우저 열기
            try {
                KakaoCustomTabsClient.openWithDefault(requireContext(), sharerUrl)
            } catch(e: UnsupportedOperationException) {
                // Chrome 브라우저가 없을 때 예외처리
            }
            // 2. CustomTabs으로 디바이스 기본 브라우저 열기
            try {
                KakaoCustomTabsClient.open(requireContext(), sharerUrl)
            } catch (e: ActivityNotFoundException) {
                // 인터넷 브라우저가 없을 때 예외처리
            }
        }
    }
}