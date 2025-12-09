package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : BaseViewModel() {

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

    private val _customer = MutableStateFlow<HomeBean?>(null)
    val customer: StateFlow<HomeBean?> = _customer

    fun getCustomer() {
        launch({ UserRepository.getHomeUnCertData() }) {
            _customer.value = it
        }
    }
}
