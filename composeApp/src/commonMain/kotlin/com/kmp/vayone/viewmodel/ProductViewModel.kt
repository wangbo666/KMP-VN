package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.ProductDetailBean
import com.kmp.vayone.data.remote.UserRepository
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
}