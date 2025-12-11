package com.kmp.vayone

import com.kmp.vayone.data.CacheManager

/**
 * Helper functions for Swift to call Kotlin CacheManager methods
 * These functions are exposed at package level for easier Swift interop
 */
fun saveAppsFlyerUID(uid: String) {
    CacheManager.saveAppsFlyerUID(uid)
}

fun saveAfSource(source: String) {
    CacheManager.saveAfSource(source)
}

fun saveAppFlyer(json: String) {
    CacheManager.saveAppFlyer(json)
}




