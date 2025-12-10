package com.kmp.vayone.data.remote

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.LoginBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.SignBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

// 使用示例
object UserRepository {
    private val networkManager = NetworkManager(
        baseUrl = CacheManager.HTTP_HOST,
        isDebug = CacheManager.isDebug
    )

    suspend fun getSecret(): ApiResponse<SignBean?> {
        return networkManager.get("api/user/app/common/secret")
    }

    suspend fun getHomeUnCertData(): ApiResponse<HomeBean?> {
        return networkManager.post("api/loan/app/common/index")
    }

    suspend fun sendOTP(phone: String): ApiResponse<Boolean?> {
        return networkManager.post("api/user/app/login/sms", ParamBean(phone = phone))
    }

    suspend fun login(paramBean: ParamBean): ApiResponse<LoginBean?> {
        return networkManager.post("api/user/app/login", paramBean)
    }
}