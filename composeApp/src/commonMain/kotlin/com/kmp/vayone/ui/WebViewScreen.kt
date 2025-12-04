package com.kmp.vayone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import com.kmp.vayone.ui.widget.TopBar
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_FC7700
import theme.white

@Composable
fun WebViewScreen(
    title: String,
    url: String,
    onBack: () -> Unit,
) {
    val webViewState = rememberWebViewState(url)
    val loadingState = webViewState.loadingState
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(white)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopBar(title = title) {
                onBack()
            }
        }) {
        Column(modifier = Modifier.fillMaxSize().background(white).padding(it)) {
            if (webViewState.isLoading) {
                when (loadingState) {
                    is com.multiplatform.webview.web.LoadingState.Loading -> {
                        LinearProgressIndicator(
                            progress = loadingState.progress,
                            modifier = Modifier.fillMaxWidth(),
                            color = C_FC7700,
                        )
                    }

                    else -> { /* 不显示进度条 */
                    }
                }
            }

            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize().background(white)
            )
        }
    }
}

@Preview
@Composable
fun PreWebView() {
    WebViewScreen("Test", "https://www.baidu.com/") {

    }
}