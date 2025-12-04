package com.kmp.vayone

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.HomeScreen
import com.kmp.vayone.ui.LoginScreen
import com.kmp.vayone.ui.PrivacyScreen
import com.kmp.vayone.ui.SplashScreen
import com.kmp.vayone.ui.WebViewScreen

@Composable
fun App() {

    // back stack（第一个为初始页面）
    var backStack by remember { mutableStateOf(listOf<Screen>(Screen.Splash)) }

    // 当前页面永远是栈顶
    val currentScreen = backStack.last()

    // 跳转：push
    fun navigate(to: Screen) {
        backStack = backStack + to
    }

    fun navigateToWebView(url: String, title: String) {
        backStack = backStack + Screen.WebView(url, title)
    }

    // 返回：pop
    fun goBack() {
        if (backStack.size > 1) {
            backStack = backStack.dropLast(1)
        }
    }

    MaterialTheme {
        when (currentScreen) {
            Screen.Splash ->
                SplashScreen { navigate(it) }

            Screen.Privacy ->
                PrivacyScreen(onBack = {
                    exitApp()
                }) {
                    navigate(it)
                }

            Screen.Login ->
                LoginScreen { navigate(it) }

            Screen.Home ->
                HomeScreen { navigate(it) }

            is Screen.WebView -> {
                WebViewScreen(currentScreen.title, currentScreen.url) {
                    goBack()
                }
            }

        }
    }
}