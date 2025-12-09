package com.kmp.vayone.data.remote

import com.kmp.vayone.currentTimeMillis
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.CacheManager.APPCODE
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.util.toMD5
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 * 签名拦截器插件 - 完整复刻 OkHttp SignInterceptor
 */
val SignInterceptorPlugin = createClientPlugin("SignInterceptor") {
    onRequest { request, content ->
        val requestUrl = request.url.toString()

        // 跳过埋点请求
        if (requestUrl.contains(CacheManager.HTTP_HOST)) {
            return@onRequest
        }

        // 根据请求类型提取 body JSON
        val bodyJson = when (content) {
            // JSON 请求
            is OutgoingContent.ByteArrayContent -> {
                val contentTypeStr = content.contentType?.contentType ?: ""
                if (contentTypeStr == "application" && content.contentType?.contentSubtype == "json") {
                    content.bytes().decodeToString()
                } else {
                    ""
                }
            }

            // Multipart 请求
            (content is OutgoingContent.WriteChannelContent) -> {
                // 从 attributes 中获取 formData（需要在发送时存储）
                val multipartData = request.attributes.getOrNull(MultipartDataKey)
                if (multipartData != null) {
                    extractMultipartParamsForSign(multipartData, request.url.parameters)
                } else {
                    ""
                }
            }

            else -> ""
        }

        // 生成签名
        val (sign, timestamp) = generateSign(bodyJson)

        // 添加签名到 header
        request.headers {
            append("sign", sign)
            append("timestamp", timestamp)
        }
    }
}

// 用于存储 Multipart 数据的 AttributeKey
private val MultipartDataKey = AttributeKey<List<PartData>>("MultipartData")

/**
 * 提取 Multipart 参数用于签名
 * 完全复刻原逻辑：
 * 1. 跳过文件字段（filename="..." 的部分）
 * 2. 只提取文本字段
 * 3. 排除 eventFile 字段
 * 4. 合并 URL query 参数
 */
private fun extractMultipartParamsForSign(
    formData: List<PartData>,
    urlParameters: ParametersBuilder
): String {
    val paramMap = mutableMapOf<String, Any?>()

    // 遍历 multipart parts
    for (part in formData) {
        when (part) {
            is PartData.FormItem -> {
                // 文本字段：提取 name 和 value
                val name = part.name ?: continue
                val value = part.value

                // 排除空值和 eventFile
                if (value.isNotBlank() && name != "eventFile") {
                    paramMap[name] = value
                }
            }
            is PartData.FileItem -> {
                // 文件字段：跳过（不参与签名）
//                LogUtil.e("Skipping file field: ${part.name}")
            }
            is PartData.BinaryItem -> {
                // 二进制字段：跳过
//                LogUtil.e("Skipping binary field: ${part.name}")
            }

            else -> {}
        }
    }

    // 合并 URL 上的 query 参数
    urlParameters.build().forEach { key, values ->
        paramMap[key] = values.firstOrNull()
    }

    // 转换为 JSON 字符串
    return Json.encodeToString(paramMap)
}

/**
 * 生成签名
 * 完全复刻原逻辑：
 * 1. 获取当前时间戳
 * 2. 处理空 body（使用 ParamBean）
 * 3. 深度排序 JSON
 * 4. 拼接签名原文：APPCODE.md5 + "*|*" + st + "*|*" + sortedJson + "*|*" + timestamp
 * 5. 移除 emoji
 * 6. 对签名原文进行 MD5
 */
private fun generateSign(bodyJson: String): Pair<String, String> {
    val timestamp = currentTimeMillis().toString()

    // 处理空 body
    val finalJson = if (bodyJson.isBlank() || bodyJson == "{}") {
        Json.encodeToString(ParamBean())
    } else {
        bodyJson
    }

    // 深度排序 JSON
    val sortedJson = sortJsonString(finalJson)

    // 拼接签名原文
    val raw = (APPCODE.toMD5() + "*|*" +
            CacheManager.getSt() + "*|*" +
            sortedJson + "*|*" +
            timestamp)
        // 移除 emoji（Unicode 范围：U+D83C-U+DBFF, U+DC00-U+DFFF）
        .replace(Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+"), "")

    // 对签名原文进行 MD5
    val sign = raw.toMD5()

    return Pair(sign, timestamp)
}

/**
 * 深度排序 JSON 字符串
 * 将 JSON 对象的 key 按字母顺序排序，支持嵌套对象和数组
 */
private fun sortJsonString(jsonStr: String): String {
    return try {
        val jsonElement = Json.parseToJsonElement(jsonStr)
        val sorted = sortJsonElement(jsonElement)
        Json.encodeToString(sorted)
    } catch (e: Exception) {
//        LogUtil.e("sortJsonString failed: ${e.message}")
        jsonStr
    }
}

/**
 * 递归排序 JsonElement
 * 复刻原 sortJson(JSONObject) 逻辑
 */
private fun sortJsonElement(element: JsonElement): JsonElement {
    return when (element) {
        is JsonObject -> {
            // 对象：按 key 排序
            val sortedEntries = element.toList().sortedBy { it.first }
            val sortedMap = sortedEntries.associate { (key, value) ->
                key to sortJsonElement(value)
            }
            JsonObject(sortedMap)
        }
        is JsonArray -> {
            // 数组：递归处理每个元素
            JsonArray(element.map { sortJsonElement(it) })
        }
        else -> {
            // 基本类型：直接返回
            element
        }
    }
}