package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.remote.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class MainViewModel : BaseViewModel() {

    private val _homeAuthResult = MutableSharedFlow<HomeLoanBean?>(replay = 1)
    val homeAuthResult: SharedFlow<HomeLoanBean?> = _homeAuthResult
    fun getHomeAuthData(isShowLoading: Boolean = false) {
        launch({ UserRepository.getHomeAuthData() }, isShowLoading) {
            _homeAuthResult.tryEmit(it)
        }
    }
}