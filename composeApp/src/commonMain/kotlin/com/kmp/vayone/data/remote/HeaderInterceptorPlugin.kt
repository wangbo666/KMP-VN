package com.kmp.vayone.data.remote

import com.kmp.vayone.data.CacheManager.getLanguage
import com.kmp.vayone.data.CacheManager.getToken
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 * Header 拦截器插件
 */
val HeaderInterceptorPlugin = createClientPlugin("HeaderInterceptor") {
    onRequest { request, _ ->
        request.headers {
            append("Content-Type", "application/json")
            append("Content-Encoding", "gzip")
            append("User-Agent", "IOS")

            // 语言设置
            val lang = if (getLanguage() == "vi") "vi_VN" else "en_US"
            append("lang", lang)

            // Token
            val token = getToken()
            if (token.isNotBlank()) {
                append("Authorization", token)
            }
        }
    }
}