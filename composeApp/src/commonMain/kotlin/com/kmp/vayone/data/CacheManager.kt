package com.kmp.vayone.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
