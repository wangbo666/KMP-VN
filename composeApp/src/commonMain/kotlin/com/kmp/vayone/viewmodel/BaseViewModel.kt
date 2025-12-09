package com.kmp.vayone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.remote.ApiResponse
import com.kmp.vayone.data.remote.NetworkManager
import com.kmp.vayone.data.remote.NetworkResult
import com.kmp.vayone.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    fun showLoading() {
//        App.appViewModel.isShowLoading.value = true
    }

    fun hideLoading() {
//        App.appViewModel.isShowLoading.value = false
    }

    fun <T> launch(
        block: suspend () -> NetworkResult<ApiResponse<T>>,
        isShowLoading: Boolean = false,
        onError: suspend (ApiResponse<*>) -> Boolean = { false },
        onSuccess: suspend (T?) -> Unit,
    ): Job {
        return viewModelScope.launch {
            if (isShowLoading) showLoading()
            try {
                when (val result = withContext(Dispatchers.IO) { block() }) {
                    is NetworkResult.Success -> {
                        onSuccess(result.data.data)
                    }

                    is NetworkResult.Error -> {
                        onError(ApiResponse(result.code, result.message, null))
                    }

                    is NetworkResult.Exception -> {
                        onError(ApiResponse(-1, result.exception.message ?: "", null))
                    }
                }
            } catch (e: Exception) {
                ("HttpException:" + e.message).log()
                if (e.message?.contains("End of input at line") == true
                    || e.message?.contains("cancelled") == true
                ) return@launch
            } finally {
                if (isShowLoading) hideLoading()
            }
        }
    }
}