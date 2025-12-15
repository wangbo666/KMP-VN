package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.AuthBean
import com.kmp.vayone.data.BankCardBean
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.KycConfigBean
import com.kmp.vayone.data.KycInfoBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.data.UserAuthBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.ui.widget.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CertViewModel : BaseViewModel() {

    private val _loadingState = MutableStateFlow<UiState>(UiState.Loading)
    val loadingState: StateFlow<UiState> = _loadingState

    private val _authList = MutableStateFlow<List<AuthBean>>(arrayListOf())
    val authList: StateFlow<List<AuthBean>> = _authList

    fun getAuthStatus(isShowLoading: Boolean = false) {
        if (!isShowLoading) {
            _loadingState.value = UiState.Loading
        }
        launch({ UserRepository.getAuthStatus() }, onError = {
            if (!isShowLoading) {
                _loadingState.value = UiState.Error()
            }
            true
        }, isShowLoading = isShowLoading) { auth ->
            getAuthConfig(onError = {
                if (!isShowLoading) {
                    _loadingState.value = UiState.Error()
                }
            }) { configList ->
                val list = ArrayList<AuthBean>()
                configList.forEach { config ->
                    if (config == "BANK" && auth?.bankCardState != "30") {
                        return@forEach
                    }
                    list.add(
                        AuthBean(
                            title = getAuthTitle(config),
                            isCertified = isAuth(config, auth)
                        )
                    )
                }
                if (auth?.workInfoState == "30") {
                    list.add(
                        AuthBean(
                            title = Strings["supple_info"],
                            isCertified = true
                        )
                    )
                }
                if (!isShowLoading) {
                    _loadingState.value = UiState.Success
                }
                _authList.value = list
            }
        }
    }

    fun getAuthConfig(onError: () -> Unit, action: (configList: List<String>) -> Unit) {
        launch({ UserRepository.getAuthConfig() }, onError = {
            onError()
            true
        }) {
            val authList = it?.authConfig?.split(",")?.filterNot { it1 -> it1.isBlank() }
                ?: listOf()
            CacheManager.saveAuthConfigList(authList)
            action.invoke(authList)
        }
    }

    private fun getAuthTitle(title: String): String {
        return when (title.uppercase()) {
            "KYC" -> Strings["kyc_certification"]
            "ID" -> Strings["personal_info"]
            "BANK" -> Strings["contact_info"]
            else -> Strings["service_provider"]
        }
    }

    private fun isAuth(title: String, userAuthBean: UserAuthBean?): Boolean {
        return when (title.uppercase()) {
            "KYC" -> userAuthBean?.kycState == "30"
            "ID" -> userAuthBean?.idState == "30"
            "BANK" -> userAuthBean?.bankCardState == "30"
            else -> userAuthBean?.telecomPermissionState == "30"
        }
    }

    private val _accountList = MutableStateFlow<List<BankCardBean>>(arrayListOf())
    val accountList: StateFlow<List<BankCardBean>> = _accountList
    fun getBankCardList(isShowLoading: Boolean = true) {
        if (!isShowLoading) {
            _loadingState.value = UiState.Loading
        }
        launch({ UserRepository.getBankcardList() }, onError = {
            if (!isShowLoading) {
                _loadingState.value = UiState.Error()
            }
            true
        }) {
            if (!isShowLoading) {
                _loadingState.value = UiState.Success
                _accountList.value = it ?: arrayListOf()
            }
        }
    }

    fun unBindCard(id: String) {
        launch({
            UserRepository.unbindCard(id)
        }, true) {
            _accountList.update { list ->
                list.filterNot { it.id.toString() == id }
            }
        }
    }

    fun setDefaultCard(id: String) {
        launch({ UserRepository.setCardDefault(id) }, true) {
            _accountList.update { list ->
                list.map { card ->
                    if (card.id.toString() == id) card.copy(isDefault = 1)
                    else card.copy(isDefault = 0)
                }
            }
        }
    }

    private val _kycConfig = MutableStateFlow<KycConfigBean?>(null)
    val kycConfig: StateFlow<KycConfigBean?> = _kycConfig
    fun getKycConfig() {
        launch({
            UserRepository.getKycConfig()
        }) {
            _kycConfig.value = it
        }
    }

    private val _kycResult = MutableStateFlow<KycInfoBean?>(null)
    val kycResult: StateFlow<KycInfoBean?> = _kycResult
    fun getKycInfo() {
        _loadingState.value = UiState.Loading
        launch({ UserRepository.getKycInfo() }, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _loadingState.value = UiState.Success
            _kycResult.value = it
        }
    }
}