package com.kmp.vayone.data.remote

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.CacheManager.APPCODE
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.version_Name
import com.kmp.vayone.util.log
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import kotlin.String

class NetworkManager(
    private val baseUrl: String = CacheManager.HTTP_HOST,
    val isDebug: Boolean = CacheManager.isDebug
) {
    val httpClient = HttpClientProvider(isDebug).client

    // GET 请求
    suspend inline fun <reified T> get(
        path: String,
        params: Map<String, String> = mapOf(
            "appCode" to APPCODE,
            "version" to version_Name,
            "mobileType" to "1",
        )
    ): ApiResponse<T> {
        return request {
            httpClient.get(buildUrl(path)) {
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }

    // POST 请求
    suspend inline fun <reified T> post(
        path: String,
        body: ParamBean? = ParamBean(version_Name, "1", APPCODE)
    ): ApiResponse<T> {
        return request {
            val bodyJson = body?.let { json.encodeToString(it) } ?: "{}"
            httpClient.post(buildUrl(path)) {
                contentType(ContentType.Application.Json)
                setBody(bodyJson)
            }
        }
    }

    // PUT 请求
    suspend inline fun <reified T> put(
        path: String,
        body: Any? = null
    ): ApiResponse<T> {
        return request {
            httpClient.put(buildUrl(path)) {
                contentType(ContentType.Application.Json)
                body?.let { setBody(it) }
            }
        }
    }

    // DELETE 请求
    suspend inline fun <reified T> delete(
        path: String
    ): ApiResponse<T> {
        return request {
            httpClient.delete(buildUrl(path))
        }
    }

    // POST Multipart（文件上传）
    suspend inline fun <reified T> postMultipart(
        path: String,
        formData: List<PartData>
    ): ApiResponse<T> {
        return request {
            httpClient.submitFormWithBinaryData(
                url = buildUrl(path),
                formData = formData
            )
        }
    }

    // 统一请求处理 - 直接返回 ApiResponse<T>
    suspend inline fun <reified T> request(
        crossinline block: suspend () -> HttpResponse
    ): ApiResponse<T> {
        return try {
            val response = block()
            val statusCode = response.status.value
            "requestStatus: $statusCode".log()

            when (statusCode) {
                in 200..299 -> {
                    // HTTP 成功，解析响应体
                    response.body<ApiResponse<T>>()
                }

                else -> {
                    // HTTP 错误 (500, 404 等)
                    try {
                        // 尝试解析服务器返回的错误 JSON
                        val errorResponse = response.body<ApiResponse<T>>()
                        errorResponse
                    } catch (e: Exception) {
                        // 如果解析失败，构造一个错误响应
                        ApiResponse(
                            code = statusCode,
                            message = response.status.description,
                            showToast = true,
                            data = null
                        )
                    }
                }
            }
        } catch (e: Exception) {
            "requestError: ${e.message}".log()
            // 异常情况返回 -1
            ApiResponse(
                code = -1,
                message = e.message ?: "Unknown error",
                showToast = true,
                data = null
            )
        }
    }

    // 构建完整 URL
    fun buildUrl(path: String): String {
        return if (path.startsWith("http")) path else "$baseUrl$path"
    }

    // 关闭客户端
    fun close() {
        httpClient.close()
    }
}