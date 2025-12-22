package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.OrderBean
import com.kmp.vayone.data.OrderDetailBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.TogetherRepaymentBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.ui.widget.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class OrderViewModel : BaseViewModel() {

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

    private val _orderList = MutableStateFlow<List<OrderBean>>(arrayListOf())
    val orderList: StateFlow<List<OrderBean>> = _orderList
    fun getOrderList() {
        _loadingState.value = UiState.Loading
        launch({ UserRepository.getOrderList() }, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _loadingState.value = UiState.Success
            _orderList.value = it ?: arrayListOf()
        }
    }

    private val _orderDetailResult = MutableStateFlow<OrderDetailBean?>(null)
    val orderDetailResult: StateFlow<OrderDetailBean?> = _orderDetailResult
    fun getOrderDetail(id: Long?) {
        _loadingState.value = UiState.Loading
        launch({ UserRepository.getOrderDetail(id) }, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _loadingState.value = UiState.Success
            _orderDetailResult.value = it
        }
    }

    private val _buttonResult = MutableStateFlow<String?>(null)
    val buttonResult: StateFlow<String?> = _buttonResult
    fun showButtonAndBorrow() {
        launch({ UserRepository.showRepaymentBorrow() }) {
            _buttonResult.value = it?.reloanButtonSign
        }
    }

    private val _installmentRepayResult = MutableSharedFlow<TogetherRepaymentBean?>(replay = 1)
    val installmentRepayResult: SharedFlow<TogetherRepaymentBean?> = _installmentRepayResult
    fun installmentRepay(paramBean: ParamBean) {
        launch({ UserRepository.installmentRepay(paramBean) }, true) {
            _installmentRepayResult.tryEmit(it)
        }
    }

    fun repayAndBorrow(id: Long?) {
        launch({ UserRepository.repayAndBorrow(id) }, true) {
        }
    }
}