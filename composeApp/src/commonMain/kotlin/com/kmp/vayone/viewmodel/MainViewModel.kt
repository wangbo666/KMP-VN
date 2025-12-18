package com.kmp.vayone.viewmodel

import com.kmp.vayone.data.BannerBean
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.MessageBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.ProductDetailBean
import com.kmp.vayone.data.UserAuthBean
import com.kmp.vayone.data.remote.UserRepository
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.isLoggedIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : BaseViewModel() {
    private val _loadingState = MutableStateFlow<UiState>(UiState.Loading)
    val loadingState: StateFlow<UiState> = _loadingState
    private val _homeAuthResult = MutableStateFlow<HomeLoanBean?>(null)
    val homeAuthResult: StateFlow<HomeLoanBean?> = _homeAuthResult

    private val _paybackResult = MutableSharedFlow<HomeLoanBean?>(replay = 1)
    val paybackResult: SharedFlow<HomeLoanBean?> = _paybackResult

    private val _homeProducts = MutableStateFlow<List<ProductBean>>(listOf())
    val homeProducts: StateFlow<List<ProductBean>> = _homeProducts
    fun getHomeAuthData(isShowLoading: Boolean = false) {
        launch({ UserRepository.getHomeAuthData() }, isShowLoading, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            if (isShowLoading) {
                _paybackResult.tryEmit(it)
            } else {
                _loadingState.value = UiState.Success
                _homeAuthResult.value = it
                val productList = ArrayList<ProductBean>()
                productList.addAll(
                    (it?.showProducts ?: arrayListOf()).onEach { it1 -> it1.canApply = true })
                productList.addAll(
                    (it?.canNotApplyProducts ?: arrayListOf()).onEach { it1 ->
                        it1.canApply = false
                    })
                _homeProducts.value = productList
            }
        }
    }

    private val _homeUnAuthResult = MutableStateFlow<HomeBean?>(null)
    val homeUnAuthResult: StateFlow<HomeBean?> = _homeUnAuthResult

    fun showLoadingBox() {
        _loadingState.value = UiState.Loading
    }

    fun getHomeUnAuthData() {
        launch({ UserRepository.getHomeUnCertData() }, true, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _loadingState.value = UiState.Success
            _homeUnAuthResult.value = it
        }
    }

    private val _isCert = MutableStateFlow<Boolean>(false)
    val isCert: StateFlow<Boolean> = _isCert

    private val _authState = MutableStateFlow<UserAuthBean?>(null)
    val authState: StateFlow<UserAuthBean?> = _authState
    fun getAuthStatus() {
        if (!isLoggedIn()) {
            _isCert.value = false
            getHomeUnAuthData()
            return
        }
        launch({ UserRepository.getAuthStatus() }, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _authState.value = it
            getAuthConfig { configList ->
                _isCert.value = (it?.isAuthPass(configList) == true) && isLoggedIn()
                if (_isCert.value) {
                    getHomeAuthData(false)
                } else {
                    getHomeUnAuthData()
                }
            }
        }
    }

    fun getAuthConfig(action: (configList: List<String>) -> Unit) {
        launch({ UserRepository.getAuthConfig() }, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            val authList = it?.authConfig?.split(",")?.filterNot { it1 -> it1.isBlank() }
                ?: listOf()
            CacheManager.saveAuthConfigList(authList)
            action.invoke(authList)
        }
    }

    private val _bannerList = MutableStateFlow<List<BannerBean>?>(null)
    val bannerList: StateFlow<List<BannerBean>?> = _bannerList
    fun getBannerList() {
        launch({ UserRepository.getBannerList() }) {
            _bannerList.value = it
        }
    }

    private val _messageList = MutableStateFlow<List<MessageBean>?>(null)
    val messageList: StateFlow<List<MessageBean>?> = _messageList
    fun getMessageList() {
        _loadingState.value = UiState.Loading
        launch({ UserRepository.getMessageList() }, onError = {
            _loadingState.value = UiState.Error()
            true
        }) {
            _messageList.value = it?.list ?: arrayListOf()
            _loadingState.value = UiState.Success
        }
    }

    fun markMessagesRead(ids: List<Long>) {
        launch({ UserRepository.markMessagesRead(ids) }) {
            val newList = _messageList.value?.map { msg ->
                if (ids.contains(msg.id)) {
                    msg.copy(readStatus = 1)  // 更新
                } else {
                    msg                    // 不更新
                }
            }
            _messageList.value = newList
        }
    }

    private val _productDetailResult = MutableSharedFlow<ProductDetailBean?>(replay = 1)
    val productDetailResult: SharedFlow<ProductDetailBean?> = _productDetailResult
    fun getProductDetail(
        id: String?,
        amount: String?
    ) {
        launch({
            UserRepository.getProductDetail(
                ParamBean(
                    productId = id, amount = amount
                )
            )
        }, true) {
            _productDetailResult.tryEmit(it)
        }
    }
}