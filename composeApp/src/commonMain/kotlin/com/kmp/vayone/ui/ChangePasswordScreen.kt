package com.kmp.vayone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.TopBar
import theme.white


@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding(), topBar = {
        TopBar(Strings["change_password"]) {
            onBack()
        }
    }) {
        Column(
            modifier = Modifier.fillMaxSize().background(white).padding(it)
        ) {
        }
    }
}