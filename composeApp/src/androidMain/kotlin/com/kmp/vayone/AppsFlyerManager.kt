package com.kmp.vayone

import android.content.Context
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.util.log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

actual class AppsFlyerManager(private val context: Context) {
    private var isInitialized = false

    actual fun initAppsFlyer(devKey: String) {
        if (isInitialized) return
        isInitialized = true

        val listener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(map: MutableMap<String, Any>?) {
                if (map == null) return

                for ((key, value) in map) {
                    if (key == "media_source") {
                        CacheManager.saveAfSource(value.toString())
                    }
                    ("AppFlyer:$key=${map[key]}").log()
                }

                // Convert map to JSON string
                val json = Json { 
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                }
                val jsonObject = buildJsonObject {
                    map.forEach { (key, value) ->
                        put(key, value.toString())
                    }
                }
                CacheManager.saveAppFlyer(json.encodeToString(JsonObject.serializer(), jsonObject))
            }

            override fun onConversionDataFail(p0: String?) {
                ("AppFlyerFailed:$p0").log()
            }

            override fun onAppOpenAttribution(map: Map<String, String>) {
                // Handle app open attribution
            }

            override fun onAttributionFailure(s: String) {
                ("AppFlyerAttributionFailure:$s").log()
            }
        }

        AppsFlyerLib.getInstance().init(devKey, listener, context)
        AppsFlyerLib.getInstance().setCollectAndroidID(false)
        AppsFlyerLib.getInstance().setCollectIMEI(false)
        AppsFlyerLib.getInstance().start(context)
        AppsFlyerLib.getInstance().registerConversionListener(context, listener)

        // Google referrer
        InstallReferrerClient.newBuilder(context).build().let { referrerClient ->
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val ref = referrerClient.installReferrer?.installReferrer
                        ("ref:$ref").log()
                        CacheManager.saveRefer(ref ?: "")
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Handle disconnection
                }
            })
        }
    }

    actual fun getAppsFlyerUID(): String? {
        return try {
            AppsFlyerLib.getInstance().getAppsFlyerUID(context)
        } catch (e: Exception) {
            null
        }
    }

    actual fun getAppFlyer(): String? {
        return CacheManager.getAppFlyer().takeIf { it.isNotEmpty() }
    }

    actual fun getReferrer(): String? {
        return CacheManager.getRefer().takeIf { it.isNotEmpty() }
    }
}

actual fun getAppsFlyerManager(): AppsFlyerManager? {
    return try {
        AndroidApp.appsFlyerManager
    } catch (e: Exception) {
        null
    }
}

