package com.kmp.vayone.data.remote

import com.kmp.vayone.currentTimeMillis
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.CacheManager.APPCODE
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.version_Name
import com.kmp.vayone.mobileType
import com.kmp.vayone.util.log
import com.kmp.vayone.util.toMD5
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.serialization.json.*

// 用于存储 Multipart 数据的 AttributeKey
val MultipartDataKey = AttributeKey<List<PartData>>("MultipartData")

/**
 * 签名拦截器插件 - 完整复刻 OkHttp SignInterceptor
 */
val SignInterceptorPlugin = createClientPlugin("SignInterceptor") {
    onRequest { request, content ->
        val requestUrl = request.url.toString()

        // 跳过埋点请求
        if (requestUrl.contains(CacheManager.TRACK_HOST)) {
            return@onRequest
        }
        "contentTAG:$content".log()
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

            // Multipart 请求 - 从 attributes 或者使用临时变量传递
            is MultiPartFormDataContent -> {
                "Detected MultiPartFormDataContent".log()
                // 尝试从 attributes 获取
                val formDataParts = request.attributes.getOrNull(MultipartDataKey)
                if (formDataParts != null) {
                    extractMultipartParamsForSign(formDataParts, request.url.parameters)
                } else {
                    "Warning: formData not found in attributes".log()
                    ""
                }
            }

            is ParamBean -> json.encodeToString(ParamBean.serializer(), content)

            else -> {
                try {
                    json.encodeToString(content)
                } catch (e: Exception) {
                    "Exception:${e.message}".log()
                    ""
                }
            }
        }

        "bodyJson for sign: $bodyJson".log()

        // 生成签名
        val (sign, timestamp) = generateSign(bodyJson)

        // 添加签名到 header
        request.headers {
            append("sign", sign)
            append("timestamp", timestamp)
        }
    }
}

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
    "=== extractMultipartParamsForSign Start ===".log()

    // ⭐ 关键修改：使用 JsonObject 而不是 Map<String, Any?>
    val jsonElements = mutableMapOf<String, JsonElement>()

    for (part in formData) {
        when (part) {
            is PartData.FormItem -> {
                val name = part.name ?: continue
                val value = part.value
                "FormItem found: name=$name, value=$value".log()

                if (value.isNotBlank() && name != "eventFile" && name != "image") {
                    // 直接存为 JsonPrimitive，避免序列化 Any 类型
                    jsonElements[name] = JsonPrimitive(value)
                }
            }

            is PartData.FileItem -> {
                "Skipping FileItem: ${part.name}".log()
            }

            is PartData.BinaryItem -> {
                "Skipping BinaryItem: ${part.name}".log()
            }

            else -> {
                "Unknown part: ${part::class.simpleName}".log()
            }
        }
    }

    // 合并 URL 参数
    urlParameters.build().forEach { key, values ->
        val value = values.firstOrNull()
        if (value != null) {
            jsonElements[key] = JsonPrimitive(value)
            "URL param: $key=$value".log()
        }
    }

    // ⭐ 使用 JsonObject 构建 JSON 字符串
    val jsonObject = JsonObject(jsonElements)
    val result = jsonObject.toString()

    "extractMultipartParamsForSign result: $result".log()
    "=== extractMultipartParamsForSign End ===".log()
    return result
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
val json = Json {
    encodeDefaults = true
    explicitNulls = false
    isLenient = true
}

private fun generateSign(bodyJson: String): Pair<String, String> {
    val timestamp = currentTimeMillis().toString()

    "=== Sign Generation Start ===".log()
    "Input bodyJson: $bodyJson".log()

    // 处理空 body
    val finalJson = if (bodyJson.isBlank() || bodyJson == "{}") {
        val result = json.encodeToString(ParamBean(version_Name, mobileType(), APPCODE))
        "Using default ParamBean: $result".log()
        result
    } else {
        bodyJson
    }

    "finalJson: $finalJson".log()

    // 深度排序 JSON
    val sortedJson = sortJsonString(finalJson)
    "sortedJson: $sortedJson".log()

    // 拼接签名原文
    val raw = (APPCODE.toMD5() + "*|*" +
            CacheManager.getSt() + "*|*" +
            sortedJson + "*|*" +
            timestamp)
        // 移除 emoji（Unicode 范围：U+D83C-U+DBFF, U+DC00-U+DFFF）
        .replace(Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+"), "")

    "raw: $raw".log()

    // 对签名原文进行 MD5
    val sign = raw.toMD5()
    "sign: $sign".log()
    "=== Sign Generation End ===".log()

    return Pair(sign, timestamp)
}

/**
 * 深度排序 JSON 字符串
 * 将 JSON 对象的 key 按字母顺序排序，支持嵌套对象和数组
 */
private fun sortJsonString(jsonStr: String): String {
    "sortJsonStr input: $jsonStr".log()
    return try {
        val jsonElement = json.parseToJsonElement(jsonStr)
        val sorted = sortJsonElement(jsonElement)
        val finalJson = json.encodeToString(sorted)
        "sortJsonString output: $finalJson".log()
        finalJson
    } catch (e: Exception) {
        "sortJsonString failed: ${e.message}".log()
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