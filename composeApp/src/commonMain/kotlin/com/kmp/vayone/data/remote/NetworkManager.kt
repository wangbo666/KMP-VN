package com.kmp.vayone.data.remote

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.CacheManager.APPCODE
import com.kmp.vayone.data.version_Name
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
    ): NetworkResult<T> {
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
        body: Any? = null
    ): NetworkResult<T> {
        return request {
            httpClient.post(buildUrl(path)) {
                contentType(ContentType.Application.Json)
                body?.let { setBody(it) }
            }
        }
    }

    // PUT 请求
    suspend inline fun <reified T> put(
        path: String,
        body: Any? = null
    ): NetworkResult<T> {
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
    ): NetworkResult<T> {
        return request {
            httpClient.delete(buildUrl(path))
        }
    }

    // POST Multipart（文件上传）
    suspend inline fun <reified T> postMultipart(
        path: String,
        formData: List<PartData>
    ): NetworkResult<T> {
        return request {
            httpClient.submitFormWithBinaryData(
                url = buildUrl(path),
                formData = formData
            )
        }
    }

    // 统一请求处理
    suspend inline fun <reified T> request(
        crossinline block: suspend () -> HttpResponse
    ): NetworkResult<T> {
        return try {
            val response = block()
            when (response.status.value) {
                in 200..299 -> {
                    val data = response.body<T>()
                    NetworkResult.Success(data)
                }

                else -> {
                    NetworkResult.Error(
                        code = response.status.value,
                        message = response.status.description
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
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