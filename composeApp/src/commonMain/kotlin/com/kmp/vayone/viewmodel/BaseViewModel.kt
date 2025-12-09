package com.kmp.vayone.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.vayone.data.remote.ApiResponse
import com.kmp.vayone.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun showLoading() {
        _isLoading.value = true
    }

    fun hideLoading() {
        _isLoading.value = false
    }

    private val _errorEvent = MutableSharedFlow<ApiResponse<*>>(replay = 0)
    val errorEvent = _errorEvent

    fun <T> launch(
        block: suspend () -> ApiResponse<T?>,
        isShowLoading: Boolean = false,
        isShowErrorToast: Boolean = true,
        onError: suspend (ApiResponse<*>) -> Unit = {},
        onSuccess: suspend (T?) -> Unit,
    ): Job {
        return viewModelScope.launch {
            if (isShowLoading) showLoading()
            try {
                val response = withContext(Dispatchers.IO) { block() }
                "responseCode: ${response.code}, message: ${response.message}".log()
                // 检查业务层状态码
                if (response.code == 200) {
                    // 成功
                    onSuccess(response.data)
                } else {
                    // 失败 (500, -1 或其他)
                    if (isShowErrorToast) {
                        _errorEvent.emit(response)
                        "errorResponse: ${response.code}, message: ${response.message}".log()
                    }
                    onError(response)
                }
            } catch (e: Exception) {
                ("HttpException: ${e.message}").log()
                val errorResponse = ApiResponse<T>(
                    code = -1,
                    message = e.message ?: "Unknown error",
                    showToast = isShowErrorToast,
                    data = null
                )
                if (isShowErrorToast) {
                    _errorEvent.emit(errorResponse)
                }
                onError(errorResponse)
            } finally {
                if (isShowLoading) hideLoading()
            }
        }
    }

    fun clearErrorResponse() {
//        _errorResponse.value = null
    }
}