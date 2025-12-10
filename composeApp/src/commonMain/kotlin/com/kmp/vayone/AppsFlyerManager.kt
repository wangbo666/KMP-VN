package com.kmp.vayone

expect class AppsFlyerManager {
    fun initAppsFlyer(devKey: String)
    fun getAppsFlyerUID(): String?
    fun getAppFlyer(): String?
    fun getReferrer(): String?
}

expect fun getAppsFlyerManager(): AppsFlyerManager?

