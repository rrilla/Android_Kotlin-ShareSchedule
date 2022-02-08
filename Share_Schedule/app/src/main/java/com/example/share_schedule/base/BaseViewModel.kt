package com.example.share_schedule.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

abstract class BaseViewModel: ViewModel() {

    abstract fun fetchData(): Job
}