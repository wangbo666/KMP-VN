package com.kmp.vayone.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HttpClientProvider(private val isDebug: Boolean = false) {

    val client = HttpClient {
        // JSON 序列化
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false         // 不序列化 null 值
            })
        }
        expectSuccess = false

        // 日志插件
        if (isDebug) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
//                        LogUtil.e("HttpIt -> $message")
                        println("HttpIt -> $message")
                    }
                }
                level = LogLevel.ALL
            }
        }

        // 超时配置
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 60000
        }

        // 安装自定义拦截器（按顺序执行）
        install(HeaderInterceptorPlugin)
        install(SignInterceptorPlugin)
        install(TrackInterceptorPlugin)

        // 默认请求配置
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}