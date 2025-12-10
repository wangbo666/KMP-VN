package com.kmp.vayone

import platform.Foundation.NSUserDefaults

actual class AppsFlyerManager {
    private var isInitialized = false

    actual fun initAppsFlyer(devKey: String) {
        if (isInitialized) return
        isInitialized = true
        // AppsFlyer is initialized in Swift (iOSApp.swift)
        // This method is kept for API compatibility but does nothing on iOS
    }
    private val defaults = NSUserDefaults.standardUserDefaults
    actual fun getAppsFlyerUID(): String? {
        val uid = defaults.stringForKey("appsFlyerUID") ?: ""
        println("CacheManager: Retrieved AppsFlyer UID: $uid")
        return uid

        // UID is saved to CacheManager by Swift delegate when AppsFlyer starts
//        return CacheManager.getAppsFlyerUID().takeIf { it.isNotEmpty() }
    }

    actual fun getAppFlyer(): String? {
        val data = defaults.stringForKey("appFlyerData") ?: ""
        println("CacheManager: Retrieved AppsFlyer data length: ${data.length}")
        return data
//        return CacheManager.getAppFlyer().takeIf { it.isNotEmpty() }
    }

    actual fun getReferrer(): String? {
        val refer = defaults.stringForKey("referrer") ?: ""
        println("CacheManager: Retrieved referrer: $refer")
        return refer
//        return CacheManager.getRefer().takeIf { it.isNotEmpty() }
    }
}

actual fun getAppsFlyerManager(): AppsFlyerManager? {
    return AppsFlyerManager()
}
