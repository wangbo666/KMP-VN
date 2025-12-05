package com.kmp.vayone

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.AboutUsScreen
import com.kmp.vayone.ui.ChangePasswordScreen
import com.kmp.vayone.ui.FeedbackScreen
import com.kmp.vayone.ui.HomeScreen
import com.kmp.vayone.ui.LoginScreen
import com.kmp.vayone.ui.LogoutScreen
import com.kmp.vayone.ui.PrivacyScreen
import com.kmp.vayone.ui.SettingsScreen
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
        backStack = if (to is Screen.Home) {
            listOf(to)
        } else {
            backStack + to
        }
    }
//
//    fun navigateToWebView(url: String, title: String) {
//        backStack = backStack + Screen.WebView(url, title)
//    }

    // 返回：pop
    fun goBack() {
        if (backStack.size > 1) {
            backStack = backStack.dropLast(1)
        }
    }

    var homeTabIndex by remember { mutableStateOf(0) }

    fun navigateAsRoot(to: Screen) {
        backStack = listOf(to)  // 只保留一个页面
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

            Screen.Home -> {
                HomeScreen(
                    selectedIndex = homeTabIndex,
                    onTabChange = { homeTabIndex = it })
                { navigate(it) }
            }

            Screen.AboutUs ->
                AboutUsScreen { goBack() }

            Screen.Settings ->
                SettingsScreen({ goBack() }) { navigate(it) }

            is Screen.WebView -> {
                WebViewScreen(currentScreen.title, currentScreen.url) {
                    goBack()
                }
            }

            Screen.ChangePassword ->
                ChangePasswordScreen({ goBack() }) { navigate(it) }

            Screen.Feedback ->
                FeedbackScreen { goBack() }

            Screen.Logout ->
                LogoutScreen({ goBack() }) { navigate(it) }
        }
    }
}