package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.LoginBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.data.version_Name
import com.kmp.vayone.getAppsFlyerManager
import com.kmp.vayone.getDeviceId
import com.kmp.vayone.getPhoneBrand
import com.kmp.vayone.getPhoneModel
import com.kmp.vayone.mobileType
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.isLoggedIn
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
        _loadingState.value = UiState.Loading
        launch({ UserRepository.getHomeUnCertData() }, true, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _loadingState.value = UiState.Success
            _customer.value = it
        }
    }

    private val _loadingState = MutableStateFlow<UiState>(UiState.Loading)
    val loadingState: StateFlow<UiState> = _loadingState

    private val _sendOtpResult = MutableSharedFlow<Boolean?>()
    val sendOtpResult: SharedFlow<Boolean?> = _sendOtpResult
    fun sendOTP(phone: String) {
        launch(
            { UserRepository.sendOTP(phone) },
            true,
            onError = { true }) {
            _sendOtpResult.tryEmit(it)
        }
    }

    private val _loginResult = MutableSharedFlow<LoginBean?>(replay = 1)
    val loginResult: SharedFlow<LoginBean?> = _loginResult

    fun login(
        phone: String,
        code: String?,
        password: String?,
    ) {
        launch({
            val appsFlyerManager = getAppsFlyerManager()
            val laPair = CacheManager.getLocation()
            val parm = ParamBean(
                phone = phone,
                regClient = if (mobileType() == "2") "Android" else "IOS",
                smsCode = code,
                coordinate = "${laPair.first},${laPair.second}",
                appsflyerId = appsFlyerManager?.getAppsFlyerUID(),
                content = appsFlyerManager?.getAppFlyer(),
                phoneMark = getDeviceId(),
                passwd = password?.toMD5(),
                loginType = if (code != null) 1 else 2
            )
            UserRepository.login(parm)
        }, true) {
            _loginResult.tryEmit(it)
        }
    }

    private val _postDeviceResult = MutableSharedFlow<String?>(replay = 1)
    val postDeviceResult: SharedFlow<String?> = _postDeviceResult
    fun postDeviceInfo() {
        if (!isLoggedIn()) return
        launch({
            UserRepository.postDeviceInfo(
                ParamBean(
                    phoneModel = getPhoneModel(),
                    phoneBrand = getPhoneBrand(),
                    phoneMark = getDeviceId(),
                    appVersion = version_Name,
                    regClient = if (mobileType() == "2") "Android" else "IOS"
                )
            )
        }, true) {
            _postDeviceResult.tryEmit(it)
        }
    }

    private val _setPasswordResult = MutableSharedFlow<LoginBean?>(replay = 1)
    val setPasswordResult: SharedFlow<LoginBean?> = _setPasswordResult
    fun setPassword(password: String) {
        launch({
            UserRepository.setPassword(
                ParamBean(
                    phone = CacheManager.getLoginInfo()?.phone,
                    newPasswd = password.toMD5()
                )
            )
        }, true) {
            _setPasswordResult.tryEmit(it)
        }
    }

    private val _logoutResult = MutableSharedFlow<String?>(replay = 1)
    val logoutResult: SharedFlow<String?> = _logoutResult
    fun logout() {
        launch({
            UserRepository.logout()
        }, true) {
            _logoutResult.tryEmit(it)
        }
    }

    private val _changeResult = MutableSharedFlow<LoginBean?>(replay = 1)
    val changeResult: SharedFlow<LoginBean?> = _changeResult
    fun changePassword(code: String, password: String) {
        launch({
            UserRepository.updatePassword(
                ParamBean(
                    phone = CacheManager.getLoginInfo()?.phone,
                    smsCode = code,
                    newPasswd = password.toMD5()
                )
            )
        }, true) {
            _changeResult.tryEmit(it)
        }
    }
}
