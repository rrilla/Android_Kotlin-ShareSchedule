package com.example.share_schedule.insertCalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.share_schedule.databinding.ActivityInsertBinding

class InsertActivity : AppCompatActivity() {
    lateinit var binding: ActivityInsertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initViews()
    }

    private fun initViews() {
        setTransaction()
    }

    private fun setTransaction() {
        val firstFragment = AddEventFragment()

        supportFragmentManager.commit {
            // 트랜잭션 내부 및 트랜잭션 간 작업 최적화 허용. 중복, 취소되는 작업 제거
            setReorderingAllowed(true)
            add(binding.fragmentContainerView.id, firstFragment)
//            replace(binding.fragmentContainerView.id, firstFragment)
        }
    }

    fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(binding.fragmentContainerView.id, fragment).commit()

        supportFragmentManager.commit {
            // 트랜잭션 내부 및 트랜잭션 간 작업 최적화 허용. 중복, 취소되는 작업 제거
            setReorderingAllowed(true)
            // BackStack에 이전Fragment 저장
            addToBackStack(null)
            replace(binding.fragmentContainerView.id, fragment)
        }
    }
}