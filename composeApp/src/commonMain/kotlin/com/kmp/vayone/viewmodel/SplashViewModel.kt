package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.util.log
import kotlinx.coroutines.delay

class SplashViewModel : BaseViewModel() {

    fun getSecret(action: () -> Unit) {
        launch({ UserRepository.getSecret() }, onError = {
            delay(1500)
            action()
            false
        }) {
            val secret = it?.verifySignSecret ?: ""
            CacheManager.setSt(secret)
            secret.log()
            delay(1500)
            action()
        }
    }
}
