package com.kmp.vayone.util

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.UserAuthBean
import com.kmp.vayone.data.remote.json
import com.kmp.vayone.navigation.Screen
import kotlinx.coroutines.launch

fun isCertPass(configs: Map<String, Pair<Boolean, String?>>): Boolean {
    configs.forEach { (_, pair) ->
        val (isConfig, state) = pair
        if (isConfig && state != "30") return false
    }
    return true
}

fun isLoggedIn(): Boolean = CacheManager.getToken().isNotBlank()

fun UserAuthBean.jumpCert(navigate: (Screen) -> Unit, isFromAuthPage: Boolean = true) {
    val configList = CacheManager.getAuthConfigList().filterNot { it.isBlank() }
    if (userAuthState == "30") {
        navigate(Screen.CertSuccess)
        return
    }
    if (isFromAuthPage
        && isCertPass(
            mapOf(
                "KYC" to (configList.contains("KYC") to kycState),
                "ID" to (configList.contains("ID") to idState),
                "TELECOM" to (configList.contains("TELECOM") to telecomPermissionState)
            )
        )
    ) {
        navigate(Screen.CertSuccess)
        return
    }
    configList.forEach {
        when {
            it.uppercase() == "KYC" && kycState != "30" -> {
                navigate(Screen.KycCert(false))
                return
            }

            it.uppercase() == "ID" && idState != "30" -> {
                navigate(Screen.PersonalCert(false))
                return
            }

            it.uppercase() == "BANK" && bankCardState != "30" -> {
                navigate(Screen.BankCert(false))
                return
            }

            it.uppercase() == "TELECOM" && telecomPermissionState != "30" -> {
                navigate(Screen.ServiceCert(false))
                return
            }
        }
    }
}

