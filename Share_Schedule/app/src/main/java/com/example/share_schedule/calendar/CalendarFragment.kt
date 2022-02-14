package com.example.share_schedule.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.share_schedule.calendar.myview.MonthFragmentStateAdapter
import com.example.share_schedule.MyApplication.Companion.firebaseAuth
import com.example.share_schedule.R
import com.example.share_schedule.data.db.entity.CalendarEntity
import com.example.share_schedule.databinding.FragmentCalendarBinding
import com.example.share_schedule.signin.LoginActivity
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private val sharedViewModel: ShareViewModel by activityViewModels()
    private val viewModel: CalendarViewModel by viewModels()

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthFragmentStateAdapter : MonthFragmentStateAdapter

    private val dateCalendar: Calendar by lazy {
        Calendar.getInstance()
    }

    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) { }
            else { activity?.finish()  }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initViews()
        initToolbar()
        observeData()
    }

    private fun initAdapter() {
        monthFragmentStateAdapter = MonthFragmentStateAdapter(requireActivity())
    }

    private fun initViews() {
        val autoLogin = arguments?.getBoolean(getString(R.string.autoLogin), true)
        if(autoLogin == true) {
            viewModel.fetchLocalData()
        } else {
            viewModel.fetchData()
        }
    }

    private fun initToolbar() = with(binding) {
        toolbar.calendarToolbar.setNavigationIcon(R.drawable.ic_baseline_menu)
        toolbar.calendarToolbar.setNavigationOnClickListener {
            calendarDrawerLayout.openDrawer(GravityCompat.START)
        }
        toolbar.toolbarTitle.setOnClickListener {
            selectDate()
        }
        setDrawerHeader()
    }

    private fun selectDate() {
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth)
            setDateWithSelectDate(cal)
        }
        DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    private fun setDateWithSelectDate(selectCalendar: Calendar) = with(binding) {
        val currentCalendar = Calendar.getInstance()
        Toast.makeText(requireContext(), "${selectCalendar.get(Calendar.YEAR)}년 ${selectCalendar.get(Calendar.MONTH)+1}월 ${selectCalendar.get(Calendar.DAY_OF_MONTH)}일", Toast.LENGTH_SHORT).show()
        currentCalendar.set(Calendar.YEAR, sharedViewModel.year)
        currentCalendar.set(Calendar.MONTH, sharedViewModel.month)
        monthViewPager.setCurrentItem((Int.MAX_VALUE / 2) + sharedViewModel.getPosition(selectCalendar, currentCalendar), false)
    }

    private fun setDrawerHeader() = with(binding) {
        val header = calendarNavigationView.getHeaderView(0)
        val logoutButton = header.findViewById<ImageButton>(R.id.logoutButton)
        val syncButton = header.findViewById<ImageButton>(R.id.syncButton)

        logoutButton.setOnClickListener {
            createLogoutDialog()
        }
        syncButton.setOnClickListener {
            viewModel.fetchData()
        }
    }

    private fun createLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("로그아웃 하시겠습니까?")
            .setPositiveButton("확인") { dialog, which ->
                signOut()
            }
            .setNegativeButton("취소") { _, _ -> }
        builder.show()
    }

    private fun signOut() {
        firebaseAuth.signOut()
        viewModel.signOut()
        val intent = Intent(activity, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        activity?.finish()
    }

    private fun observeData() {
        viewModel.authStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is UserRecoverableAuthIOException -> authLauncher.launch(it.intent)
                else -> Log.e("CalendarActivity", "pass")
            }
        }

        viewModel.calendarIdLiveData.observe(viewLifecycleOwner) {
            setCheckBox(it)
        }

        viewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is LoadingState.Loading -> {
                    handleLoadingState()
                }
                is LoadingState.Success -> {
                    initViewPager()
                }
                else -> {}
            }
        }
    }

    private fun setCheckBox(list: List<CalendarEntity>) = with(binding) {
        calendarNavigationView.menu.clear()
        calendarNavigationView.menu.add("캘린더 목록")
        val checkCalendarList = mutableListOf<String>()

        for(calendars in list) {
            checkCalendarList.add(calendars.id)
            val item = (calendarNavigationView.menu.add(calendars.summary).setActionView(CheckBox(context)))
            item.setIcon(R.drawable.ic_calendar)
            val checkButton = item.actionView as CheckBox
            checkButton.isChecked = true
            checkButton.setOnCheckedChangeListener { buttonView, isChecked ->
                when(isChecked) {
                    true -> {
                        checkCalendarList.add(calendars.id)
                        sharedViewModel.setCheckCalendarList(checkCalendarList)
                    }
                    false -> {
                        checkCalendarList.remove(calendars.id)
                        sharedViewModel.setCheckCalendarList(checkCalendarList)
                    }
                }
            }
        }
        sharedViewModel.setCheckCalendarList(checkCalendarList)
    }

    private fun handleLoadingState() = with(binding) {
        calendarNavigationView.getHeaderView(0).findViewById<ProgressBar>(R.id.progressBar).isVisible = true
        toolbar.toolbarProgressBar.isVisible = true
    }

    private fun initViewPager() = with(binding) {
        calendarNavigationView.getHeaderView(0).findViewById<ProgressBar>(R.id.progressBar).isVisible = false
        toolbar.toolbarProgressBar.isVisible = false
        monthViewPager.offscreenPageLimit = 3
        monthViewPager.adapter = monthFragmentStateAdapter
        monthViewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        monthFragmentStateAdapter.apply {
            monthViewPager.setCurrentItem(this.monthFragmentPosition, false)
        }

        //  뷰 페이저 전환시 애니메이션 - 툴바에 현재 페이지 날짜 출력
        monthViewPager.setPageTransformer { page, position ->
            if(position == 0.0f) {
                dateCalendar.set(Calendar.YEAR, sharedViewModel.year)
                dateCalendar.set(Calendar.MONTH, sharedViewModel.month)
                dateCalendar.add(Calendar.MONTH, (monthViewPager.currentItem-(Int.MAX_VALUE/2)))
                val date = dateCalendar.time
                val dateFormat = SimpleDateFormat("yyyy. MM", Locale.KOREA)
                toolbar.toolbarTitle.text = dateFormat.format(date)
            }
        }
    }






    override fun onOptionsItemSelected(item: MenuItem): Boolean = with(binding) {
        when(item.itemId) {
            android.R.id.home -> calendarDrawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.month = dateCalendar.get(Calendar.MONTH)
        sharedViewModel.year = dateCalendar.get(Calendar.YEAR)
        _binding = null
    }
}
