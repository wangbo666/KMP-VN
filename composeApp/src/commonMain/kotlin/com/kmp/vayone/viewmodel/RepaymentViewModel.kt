package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.ui.widget.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RepaymentViewModel : BaseViewModel() {

    private val _loadingState = MutableStateFlow<UiState>(UiState.Loading)
    val loadingState: StateFlow<UiState> = _loadingState
    private val _togetherRepaymentList = MutableStateFlow<List<ProductBean>?>(null)
    val togetherRepaymentList: StateFlow<List<ProductBean>?> = _togetherRepaymentList
    fun getTogetherRepaymentList() {
        _loadingState.value = UiState.Loading
        launch({ UserRepository.getTogetherRepaymentList() }, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _loadingState.value = UiState.Success
            _togetherRepaymentList.value = it
        }
    }
}