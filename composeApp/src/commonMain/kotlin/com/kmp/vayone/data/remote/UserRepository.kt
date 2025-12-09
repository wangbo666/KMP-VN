package com.kmp.vayone.data.remote

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.SignBean

// 使用示例
object UserRepository {
    private val networkManager = NetworkManager(
        baseUrl = CacheManager.HTTP_HOST,
        isDebug = CacheManager.isDebug
    )

    suspend fun getSecret(): NetworkResult<ApiResponse<SignBean>> {
        return networkManager.get("api/user/app/common/secret")
    }

    // GET 请求
//    suspend fun getUser(userId: String): NetworkResult<ApiResponse<User>> {
//        return networkManager.get("/users/$userId")
//    }
//
//    // POST 请求
//    suspend fun login(username: String, password: String): NetworkResult<ApiResponse<User>> {
//        val request = LoginRequest(username, password)
//        return networkManager.post("/auth/login", body = request)
//    }
//
//    // 文件上传
//    suspend fun uploadFile(filePath: String): NetworkResult<ApiResponse<String>> {
//        val formData = formData {
//            append("file", WebContent.File(filePath).readBytes(), Headers.build {
//                append(HttpHeaders.ContentType, "image/jpeg")
//                append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
//            })
//            append("name", "my-image")
//        }
//        return networkManager.postMultipart("/upload", formData)
//    }
//
//    // 处理结果
//    suspend fun handleLogin(username: String, password: String) {
//        when (val result = login(username, password)) {
//            is NetworkResult.Success -> {
//                val user = result.data.data
//                println("Login success: $user")
//            }
//            is NetworkResult.Error -> {
//                println("Error ${result.code}: ${result.message}")
//            }
//            is NetworkResult.Exception -> {
//                println("Exception: ${result.exception.message}")
//            }
//        }
//    }
}