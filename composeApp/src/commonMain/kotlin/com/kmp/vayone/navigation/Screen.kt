package com.kmp.vayone.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Privacy : Screen
    data object Home : Screen
    data object Login : Screen
    data class WebView(val title: String, val url: String) : Screen
    data object AboutUs : Screen
    data object Settings : Screen
    data object ChangePassword : Screen
    data object Logout : Screen
    data object Feedback : Screen
}