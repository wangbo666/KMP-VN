package com.kmp.vayone.util

import com.kmp.vayone.data.CacheManager

fun isCertPass(configs: Map<String, Pair<Boolean, String?>>): Boolean {
    configs.forEach { (_, pair) ->
        val (isConfig, state) = pair
        if (isConfig && state != "30") return false
    }
    return true
}

fun isLoggedIn(): Boolean = CacheManager.getToken().isNotBlank()

fun String?.toAmountString(symbol: String?): String {
    return "${this ?: ""}${symbol ?: ""}"
}