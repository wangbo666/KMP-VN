package com.kmp.vayone

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class AndroidApp : Application() {

    companion object {
        var appContext: Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}