package com.kmp.vayone

import com.kmp.vayone.data.CacheManager

actual class AppsFlyerManager {
    private var isInitialized = false

    actual fun initAppsFlyer(devKey: String) {
        if (isInitialized) return
        isInitialized = true
        // AppsFlyer is initialized in Swift (iOSApp.swift)
        // This method is kept for API compatibility but does nothing on iOS
    }

    actual fun getAppsFlyerUID(): String? {
        // UID is saved to CacheManager by Swift delegate when AppsFlyer starts
        return CacheManager.getAppsFlyerUID().takeIf { it.isNotEmpty() }
    }

    actual fun getAppFlyer(): String? {
        return CacheManager.getAppFlyer().takeIf { it.isNotEmpty() }
    }

    actual fun getReferrer(): String? {
        return CacheManager.getRefer().takeIf { it.isNotEmpty() }
    }
}

actual fun getAppsFlyerManager(): AppsFlyerManager? {
    return AppsFlyerManager()
}
