package com.kmp.vayone.data.remote

import kotlinx.serialization.Serializable

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    data class Exception(val exception: Throwable) : NetworkResult<Nothing>()
}

@Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)