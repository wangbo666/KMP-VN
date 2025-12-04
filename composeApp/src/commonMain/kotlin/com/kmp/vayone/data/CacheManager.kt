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

    // --- Login ---
    fun isLoggedIn(): Boolean =
        settings.getBoolean("isLoggedIn", false)

    fun setLoggedIn(value: Boolean) {
        settings.putBoolean("isLoggedIn", value)
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
}
