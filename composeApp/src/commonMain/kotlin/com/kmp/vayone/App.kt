package com.kmp.vayone

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.backhandler.BackHandler
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.AboutUsScreen
import com.kmp.vayone.ui.AccountCenter
import com.kmp.vayone.ui.AddAccountScreen
import com.kmp.vayone.ui.BatchRepayment
import com.kmp.vayone.ui.CertBankScreen
import com.kmp.vayone.ui.CertKycScreen
import com.kmp.vayone.ui.CertPersonalScreen
import com.kmp.vayone.ui.CertScreen
import com.kmp.vayone.ui.CertServiceScreen
import com.kmp.vayone.ui.CertSuccessScreen
import com.kmp.vayone.ui.ChangePasswordScreen
import com.kmp.vayone.ui.ContactUsScreen
import com.kmp.vayone.ui.FeedbackScreen
import com.kmp.vayone.ui.HomeScreen
import com.kmp.vayone.ui.LoanResultScreen
import com.kmp.vayone.ui.LoginScreen
import com.kmp.vayone.ui.LogoutScreen
import com.kmp.vayone.ui.LogoutSuccessScreen
import com.kmp.vayone.ui.MessageDetailScreen
import com.kmp.vayone.ui.MessageScreen
import com.kmp.vayone.ui.OrderCenterScreen
import com.kmp.vayone.ui.PrivacyScreen
import com.kmp.vayone.ui.SetPasswordScreen
import com.kmp.vayone.ui.SetPasswordSuccessScreen
import com.kmp.vayone.ui.SettingsScreen
import com.kmp.vayone.ui.SignScreen
import com.kmp.vayone.ui.SplashScreen
import com.kmp.vayone.ui.SuppleScreen
import com.kmp.vayone.ui.WebViewScreen
import com.kmp.vayone.ui.widget.ToastHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {

    // 用于判断是前进还是后退
    var isNavigatingForward by remember { mutableStateOf(true) }
    val previousStackSize = remember { mutableStateOf(1) }
    val animationDuration = 300

    // back stack（第一个为初始页面）
    var backStack by remember { mutableStateOf(listOf<Screen>(Screen.Splash)) }

    // 当前页面永远是栈顶
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    // 协程作用域
    val scope = rememberCoroutineScope()

    var homeTabIndex by remember { mutableStateOf(0) }

    // 跳转：push
    fun navigate(to: Screen) {
        isNavigatingForward = true
        currentScreen = to
        scope.launch {
            delay(animationDuration.toLong())
            backStack = if (to is Screen.Home) {
                homeTabIndex = to.selectedIndex
                listOf(to)
            } else {
                backStack + to
            }
        }
    }
//
//    fun navigateToWebView(url: String, title: String) {
//        backStack = backStack + Screen.WebView(url, title)
//    }

    // 返回：pop
    fun goBack() {
        if (backStack.size > 1) {
            isNavigatingForward = false
            val previousScreen = backStack[backStack.size - 2]
            currentScreen = previousScreen
            scope.launch {
                delay(animationDuration.toLong())
                backStack = backStack.dropLast(1)
            }
        }
    }


    fun navigateAsRoot(to: Screen) {
        isNavigatingForward = true
        currentScreen = to
        scope.launch {
            delay(animationDuration.toLong())
            backStack = listOf(to)
        }
    }


    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }


    LaunchedEffect(backStack.size) {
        isNavigatingForward = backStack.size > previousStackSize.value
        previousStackSize.value = backStack.size
    }

    MaterialTheme {
        Box {
//            if (mobileType() != "2") {
//            BackHandler(enabled = backStack.size > 1) {
//                goBack()
//            }
//            }
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (isNavigatingForward) {
                        // 前进动画：从右往左滑入
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { -it / 3 },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                    } else {
                        // 后退动画：从左往右滑出
                        slideInHorizontally(
                            initialOffsetX = { -it / 3 },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                    }
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.Splash ->
                        SplashScreen { navigate(it) }

                    Screen.Privacy ->
                        PrivacyScreen(toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        }, onBack = {
                            exitApp()
                        }) {
                            navigate(it)
                        }

                    Screen.Login ->
                        LoginScreen(
                            toast = { show, toast ->
                                showToast = show
                                toastMessage = toast
                            },
                        ) { navigate(it) }

                    is Screen.Home -> {
                        HomeScreen(
                            isFromCertSuccess = screen.isFromCertSuccess,
                            toast = { show, toast ->
                                showToast = show
                                toastMessage = toast
                            },
                            selectedIndex = homeTabIndex,
                            onTabChange = { homeTabIndex = it })
                        { navigate(it) }
                    }

                    Screen.AboutUs ->
                        AboutUsScreen { goBack() }

                    Screen.Settings ->
                        SettingsScreen(toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        }, { goBack() }) { navigate(it) }

                    is Screen.WebView -> {
                        WebViewScreen(screen.title, screen.url) {
                            goBack()
                        }
                    }

                    Screen.ChangePassword ->
                        ChangePasswordScreen(toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        }, onBack = { goBack() }) { navigate(it) }

                    Screen.Feedback ->
                        FeedbackScreen(toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        }) {
                            goBack()
                        }

                    Screen.Logout ->
                        LogoutScreen(toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        }, onBack = { goBack() }) { navigate(it) }

                    Screen.LogoutSuccess ->
                        LogoutSuccessScreen {
                            navigate(it)
                        }

                    Screen.ContactUs ->
                        ContactUsScreen(toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        }) { goBack() }

                    Screen.SetPassword -> SetPasswordScreen(toast = { show, toast ->
                        showToast = show
                        toastMessage = toast
                    }) {
                        navigate(it)
                    }

                    is Screen.SetPasswordSuccess -> SetPasswordSuccessScreen(
                        title = screen.title,
                    ) {
                        navigate(it)
                    }

                    Screen.BatchRepayment -> BatchRepayment(onBack = { goBack() }) {
                        navigate(it)
                    }

                    Screen.Message -> MessageScreen(onBack = { goBack() }) {
                        navigate(it)
                    }

                    is Screen.MessageDetail -> MessageDetailScreen(message = screen.data) {
                        goBack()
                    }

                    Screen.Cert -> CertScreen(toast = { show, toast ->
                        showToast = show
                        toastMessage = toast
                    }, onBack = { goBack() }) { navigate(it) }

                    is Screen.KycCert -> CertKycScreen(screen.isCert, toast = { show, toast ->
                        showToast = show
                        toastMessage = toast
                    }, onBack = { goBack() }) { navigate(it) }

                    is Screen.PersonalCert -> CertPersonalScreen(
                        screen.isCert,
                        toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        },
                        onBack = { goBack() }) { navigate(it) }

                    is Screen.BankCert -> CertBankScreen(screen.isCert, toast = { show, toast ->
                        showToast = show
                        toastMessage = toast
                    }, onBack = { goBack() }) { navigate(it) }

                    is Screen.ServiceCert -> CertServiceScreen(
                        screen.isCert,
                        toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        },
                        onBack = { goBack() }) { navigate(it) }

                    Screen.OrderCenter -> OrderCenterScreen(
                        toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        },
                        onBack = { goBack() }) { navigate(it) }

                    Screen.AccountCenter -> AccountCenter(
                        toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        },
                        onBack = { goBack() }) { navigate(it) }

                    Screen.CertSuccess -> CertSuccessScreen { navigate(it) }
                    Screen.AddAccount -> AddAccountScreen(
                        toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        },
                        onBack = { goBack() }) { navigate(it) }

                    is Screen.SuppleInfo -> SuppleScreen(
                        screen.isCert, screen.amount.replace(".00", ""),
                        toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        },
                        onBack = { goBack() }) { navigate(it) }

                    is Screen.Sign -> SignScreen(screen.signPageParams, toast = { show, toast ->
                        showToast = show
                        toastMessage = toast
                    }, onBack = { goBack() }) {
                        navigate(it)
                    }

                    is Screen.LoanResult -> LoanResultScreen(
                        screen.signPageParams, toast = { show, toast ->
                            showToast = show
                            toastMessage = toast
                        },
                        onBack = { goBack() }) { navigate(it) }
                }
                ToastHost(
                    message = toastMessage,
                    show = showToast,
                    onDismiss = {
                        showToast = false
                    }
                )
            }
        }
    }
}