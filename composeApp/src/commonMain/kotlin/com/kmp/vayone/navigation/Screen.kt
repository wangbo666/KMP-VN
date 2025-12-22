package com.kmp.vayone.navigation

import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.MessageBean
import com.kmp.vayone.data.ProductDetailBean
import com.kmp.vayone.ui.widget.SignPageParams

sealed interface Screen {
    data object Splash : Screen
    data object Privacy : Screen
    data class Home(val selectedIndex: Int = 0, val isFromCertSuccess: Boolean = false) : Screen
    data object Login : Screen
    data class WebView(val title: String, val url: String) : Screen
    data object AboutUs : Screen
    data object Settings : Screen
    data object ChangePassword : Screen
    data object Logout : Screen
    data object Feedback : Screen
    data object LogoutSuccess : Screen
    data object ContactUs : Screen
    data object SetPassword : Screen
    data class SetPasswordSuccess(val title: String) : Screen
    data object BatchRepayment : Screen
    data object Message : Screen
    data class MessageDetail(val data: MessageBean) : Screen
    data object Cert : Screen
    data class KycCert(val isCert: Boolean) : Screen
    data class PersonalCert(val isCert: Boolean) : Screen
    data class BankCert(val isCert: Boolean) : Screen
    data class ServiceCert(val isCert: Boolean) : Screen
    data object CertSuccess : Screen
    data object OrderCenter : Screen
    data object AccountCenter : Screen
    data object AddAccount : Screen
    data class SuppleInfo(val isCert: Boolean, val amount: String) : Screen
    data class Sign(val signPageParams: SignPageParams) : Screen
    data class LoanResult(val signPageParams: SignPageParams) : Screen
    data class Product(val productDetail: ProductDetailBean) : Screen
    data class Together(val loanData: HomeLoanBean) : Screen
    data class OrderDetail(val orderId: Long?, val isFromBatch: Boolean = false) : Screen
    data class Repayment(val orderId: String?, val orderNo: String?, val amount: String?) : Screen
}