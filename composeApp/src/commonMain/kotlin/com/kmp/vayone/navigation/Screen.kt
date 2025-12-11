package com.kmp.vayone.navigation

import com.kmp.vayone.data.MessageBean

sealed interface Screen {
    data object Splash : Screen
    data object Privacy : Screen
    data class Home(val selectedIndex: Int = 0) : Screen
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
}