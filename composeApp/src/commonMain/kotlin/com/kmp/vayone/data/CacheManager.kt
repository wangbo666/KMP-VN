package com.kmp.vayone.data

import com.kmp.vayone.data.remote.json
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer

object CacheManager {
    private val settings = Settings()

    // --- Privacy ---
    fun isAgreedPrivacy(): Boolean =
        settings.getBoolean("agreedPrivacy", false)

    fun setAgreedPrivacy(value: Boolean) {
        settings.putBoolean("agreedPrivacy", value)
    }

    fun getToken(): String = settings.getString("token", "")
    fun setToken(token: String) {
        settings.putString("token", token)
    }

    fun getSt(): String = settings.getString("secretKey", "")
    fun setSt(st: String) {
        settings.putString("secretKey", st)
    }

    fun getLanguage(): String = settings.getString("language", "en")
    fun setLanguage(lang: String) {
        settings.putString("language", lang)
    }

    fun getAuthConfigList(): List<String> {
        val info = settings.getString("configList", "")
        return try {
            json.decodeFromString<List<String>>(info)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    fun saveAuthConfigList(l: List<String>?) {
        if (l == null) {
            settings.remove("configList")
        } else {
            try {
                settings.putString("configList", json.encodeToString(l))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isSignBackHome(): Boolean {
        return settings.getBoolean("isBackHome", false)
    }

    fun setSignBackHome(b: Boolean) {
        settings.putBoolean("isBackHome", b)
    }

    // --- AppsFlyer ---
    fun saveAfSource(value: String) {
        settings.putString("afSource", value)
    }

    fun getAfSource(): String = settings.getString("afSource", "")

    fun saveAppFlyer(value: String) {
        settings.putString("appFlyer", value)
    }

    fun getAppFlyer(): String = settings.getString("appFlyer", "")

    fun saveRefer(value: String) {
        settings.putString("refer", value)
    }

    fun getRefer(): String = settings.getString("refer", "")

    fun saveAppsFlyerUID(value: String) {
        settings.putString("appsFlyerUID", value)
    }

    fun getLoginInfo(): LoginBean? {
        val info = settings.getString("loginInfo", "")
        return try {
            json.decodeFromString<LoginBean>(info)
        } catch (e: Exception) {
            null
        }
    }

    fun setLoginInfo(l: LoginBean?) {
        if (l == null) {
            settings.remove("loginInfo")
        } else {
            try {
                settings.putString("loginInfo", json.encodeToString(l))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun getLocation(): Pair<Double, Double> {
        val info = settings.getString("location", "")
        return try {
            json.decodeFromString(PairSerializer(Double.serializer(), Double.serializer()), info)
        } catch (e: Exception) {
            Pair(0.0, 0.0)
        }
    }

    fun saveLocation(p: Pair<Double, Double>) {
        val info = json.encodeToString(PairSerializer(Double.serializer(), Double.serializer()), p)
        settings.putString("location", info)
    }

    fun getAppsFlyerUID(): String = settings.getString("appsFlyerUID", "")

    const val PRIVACY_POLICY = "https://www.vayone-fast.com/agreement/protocol_privacy_index.html"
    const val AGREEMENT_ABOUT = "https://www.vayone-fast.com/agreement/about.html"
    const val AGREEMENT_REGISTER = "https://www.vayone-fast.com/agreement/register.html"

    const val HTTP_INFORMATION_COLLECTION =
        "https://www.vayone-fast.com/agreement/contact_license_agreement.html"
    const val PRIVACY_COLLECT =
        "https://www.vayone-fast.com/agreement/Information_collection_service_agreement.html"
    const val LEASE_AGREEMENT = "https://www.vayone-fast.com/agreement/leaseAgreement.html?"
    const val PAWN_AGREEMENT = "https://www.vayone-fast.com/agreement/pawnAgreement.html?"

    const val APPCODE = "vayone"

    const val isDebug = true
    val TRACK_HOST =
        if (isDebug) "http://test-burying-point.cn-hangzhou.log.aliyuncs.com/logstores/survey-staging/"
        else "https://log.vayone-fast.com/logstores/survey-prod/"
    val HTTP_HOST =
        if (isDebug) "http://vn-cash-api.cc006e2ab86b64d7e843cbb0d774deebb.cn-hangzhou.alicontainer.com/"
        else "https://log.vayone-fast.com/logstores/survey-prod/"
}


