package com.kmp.vayone.data.remote

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.CacheManager.APPCODE
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.version_Name
import com.kmp.vayone.mobileType
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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.String

class NetworkManager(
    private val baseUrl: String = CacheManager.HTTP_HOST,
) {
    val httpClient = HttpClientProvider().client

    // GET 请求
    suspend inline fun <reified T> get(
        path: String,
        params: Map<String, String> = mapOf(
            "appCode" to APPCODE,
            "version" to version_Name,
            "mobileType" to mobileType(),
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
        body: ParamBean = ParamBean(version_Name, mobileType(), APPCODE)
    ): ApiResponse<T> {
        return request {
            httpClient.post(buildUrl(path)) {
                contentType(ContentType.Application.Json)
                setBody(body)
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

    // POST Multipart（文件上传）- 增强日志版
    suspend inline fun <reified T> postMultipart(
        path: String,
        formData: List<PartData>
    ): ApiResponse<T> {
        val fullUrl = buildUrl(path)
        "=== Multipart Request Start ===".log()
        "URL: $fullUrl".log()
        "FormData parts count: ${formData.size}".log()

        return try {
            "Sending request...".log()

            val response = httpClient.submitFormWithBinaryData(
                url = fullUrl,
                formData = formData
            ) {
                attributes.put(MultipartDataKey, formData)
            }

            "Request sent successfully!".log()

            val statusCode = response.status.value
            "Response status: $statusCode".log()

            // 读取原始响应
            val bodyText = response.bodyAsText()
            "Response body length: ${bodyText.length}".log()
            "Response body: $bodyText".log()

            if (bodyText.isBlank()) {
                "ERROR: Response body is empty!".log()
                return ApiResponse(
                    code = -1,
                    message = "Server returned empty response",
                    showToast = true,
                    data = null
                )
            }

            when (statusCode) {
                in 200..299 -> {
                    "Parsing successful response...".log()
                    parseApiResponse(bodyText, statusCode, response.status.description)
                }
                else -> {
                    "Parsing error response...".log()
                    parseApiResponse(bodyText, statusCode, response.status.description)
                }
            }
        } catch (e: Exception) {
            "=== Multipart Request Failed ===".log()
            "Error type: ${e::class.simpleName}".log()
            "Error message: ${e.message}".log()
            e.printStackTrace()

            ApiResponse(
                code = -1,
                message = "Request failed: ${e.message}",
                showToast = true,
                data = null
            )
        } finally {
            "=== Multipart Request End ===".log()
        }
    }

    // 解析 API 响应的通用方法 - 增强日志版
    suspend inline fun <reified T> parseApiResponse(
        bodyText: String,
        statusCode: Int,
        statusDescription: String
    ): ApiResponse<T> {
        "=== Parse API Response ===".log()
        "Status: $statusCode - $statusDescription".log()
        "Body: $bodyText".log()

        return try {
            // 先解析为 JsonObject
            val jsonObject = json.parseToJsonElement(bodyText).jsonObject
            "JSON parsed successfully".log()

            val code = jsonObject["code"]?.jsonPrimitive?.intOrNull ?: statusCode
            val message = jsonObject["message"]?.jsonPrimitive?.contentOrNull ?: statusDescription
            val showToast = jsonObject["showToast"]?.jsonPrimitive?.booleanOrNull ?: true

            "Parsed: code=$code, message=$message".log()

            // 处理 data 字段
            val data: T? = when {
                // 如果 T 是 Unit，直接返回 Unit
                T::class == Unit::class -> {
                    "Data type is Unit, returning Unit".log()
                    @Suppress("UNCHECKED_CAST")
                    Unit as T
                }
                // 如果没有 data 字段或 data 为 null
                !jsonObject.containsKey("data") || jsonObject["data"] is JsonNull -> {
                    "Data field is null or missing".log()
                    null
                }
                // 其他情况正常解析
                else -> {
                    try {
                        "Parsing data field as ${T::class.simpleName}".log()
                        json.decodeFromJsonElement<T>(jsonObject["data"]!!)
                    } catch (e: Exception) {
                        "Parse data field error: ${e.message}".log()
                        e.printStackTrace()
                        null
                    }
                }
            }

            val result = ApiResponse(
                code = code,
                message = message,
                showToast = showToast,
                data = data
            )
            "Parse successful: $result".log()
            result

        } catch (e: Exception) {
            "=== Parse Failed ===".log()
            "Error type: ${e::class.simpleName}".log()
            "Error message: ${e.message}".log()
            e.printStackTrace()

            ApiResponse(
                code = statusCode,
                message = "Parse error: ${e.message}",
                showToast = true,
                data = null
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
                        "requestHttpError: ${e.message}".log()
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