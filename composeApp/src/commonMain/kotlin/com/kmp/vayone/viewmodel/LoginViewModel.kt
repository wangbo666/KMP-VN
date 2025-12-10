package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.LoginBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.getAppsFlyerManager
import com.kmp.vayone.mobileType
import com.kmp.vayone.util.log
import com.kmp.vayone.util.toMD5
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
        launch({ UserRepository.getHomeUnCertData() }, true) {
            _customer.value = it
        }
    }

    private val _sendOtpResult = MutableSharedFlow<Boolean?>()
    val sendOtpResult: SharedFlow<Boolean?> = _sendOtpResult
    fun sendOTP(phone: String) {
        launch({ UserRepository.sendOTP(phone) }, true) {
            _sendOtpResult.tryEmit(it)
        }
    }

    private val _loginResult = MutableSharedFlow<LoginBean?>()
    val loginResult: SharedFlow<LoginBean?> = _loginResult

    fun login(
        phone: String,
        code: String?,
        password: String?,
    ) {
        val appsFlyerManager = getAppsFlyerManager()
        val parm = ParamBean(
            phone = phone,
            regClient = if (mobileType() == "2") "Android" else "IOS",
            smsCode = code,
//            coordinate = "${laPair.first},${laPair.second}",
            appsflyerId = appsFlyerManager?.getAppsFlyerUID() ?: "",
            content = appsFlyerManager?.getAppFlyer(),
//            phoneMark = DeviceUtil.getDeviceId(),
            passwd = password?.toMD5(),
            loginType = if (code != null) 1 else 2
        )
        "appflyerId:${appsFlyerManager?.getAppsFlyerUID()}\ncontent:${appsFlyerManager?.getAppFlyer()}"
        launch({ UserRepository.login(parm) }, false) {
            _loginResult.tryEmit(it)
        }
    }
}
