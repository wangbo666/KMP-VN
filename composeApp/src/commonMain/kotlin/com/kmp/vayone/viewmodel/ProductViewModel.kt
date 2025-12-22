package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.ProductDetailBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.ui.widget.SignPageParams
import com.kmp.vayone.ui.widget.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class ProductViewModel : BaseViewModel() {
    private val _loadingState = MutableStateFlow<UiState>(UiState.Loading)
    val loadingState: StateFlow<UiState> = _loadingState
    private val _productDetailResult = MutableSharedFlow<ProductDetailBean?>(replay = 1)
    val productDetailResult: SharedFlow<ProductDetailBean?> = _productDetailResult
    fun getProductDetail(
        id: String?,
        amount: String?
    ) {
        _loadingState.value = UiState.Loading
        launch({
            UserRepository.getProductDetail(
                ParamBean(
                    productId = id, amount = amount
                )
            )
        }, true, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _loadingState.value = UiState.Success
            _productDetailResult.tryEmit(it)
        }
    }

    private val _loanResult = MutableStateFlow<List<ProductBean>?>(null)
    val loanResult: StateFlow<List<ProductBean>?> = _loanResult
    fun togetherLoan(params: SignPageParams) {
        _loadingState.value = UiState.Loading
        launch(
            {
                UserRepository.togetherLoan(
                    params.bankId.toString(),
                    params.productList!!,
                    params.signPath,
                    params.productInstallmentMap,
                    params.termIdMap
                )
            },
            onError = {
                _loanResult.tryEmit(null)
                _loadingState.value = UiState.Error()
                true
            }) {
            _loadingState.value = UiState.Success
            _loanResult.tryEmit(it)
        }
    }

    fun singleLoan(params: SignPageParams) {
        _loadingState.value = UiState.Loading
        launch(
            {
                UserRepository.singleLoan(
                    params.productId.orEmpty(),
                    params.amount.orEmpty(),
                    params.bankId.toString(),
                    params.signPath,
                    params.productInstallmentMap,
                    params.termIdMap
                )
            },
            onError = {
                _loanResult.tryEmit(null)
                _loadingState.value = UiState.Error()
                true
            }) {
            _loadingState.value = UiState.Success
            _loanResult.tryEmit(if (it == null) null else listOf(it))
        }
    }
}