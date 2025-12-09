package com.kmp.vayone.data.remote

import com.kmp.vayone.data.CacheManager
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.headers
import io.ktor.http.content.OutgoingContent

/**
 * 埋点拦截器插件
 */
val TrackInterceptorPlugin = createClientPlugin("TrackInterceptor") {
    onRequest { request, content ->
        val requestUrl = request.url.toString()

        if (requestUrl.contains(CacheManager.TRACK_HOST)) {
            request.headers {
                append("x-log-apiversion", "0.6.0")

                // 获取 body 大小
                val bodySize = when (content) {
                    is OutgoingContent.ByteArrayContent -> content.contentLength?.toString() ?: "0"
                    else -> "0"
                }
                append("x-log-bodyrawsize", bodySize)
                append("Connection", "keep-alive")
            }
        }
    }
}