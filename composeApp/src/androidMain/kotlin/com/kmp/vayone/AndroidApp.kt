package com.kmp.vayone

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class AndroidApp : Application() {

    companion object {
        var appContext: Context by Delegates.notNull()
        private var _appsFlyerManager: AppsFlyerManager? = null
        val appsFlyerManager: AppsFlyerManager
            get() = _appsFlyerManager ?: throw IllegalStateException("AppsFlyerManager not initialized")
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        // Initialize AppsFlyer
        _appsFlyerManager = AppsFlyerManager(appContext)
        _appsFlyerManager?.initAppsFlyer("gW2odiT5txpMruKrCQkrTb")
    }
}