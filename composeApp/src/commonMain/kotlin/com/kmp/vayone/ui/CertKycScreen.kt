package com.kmp.vayone.ui

import androidx.compose.runtime.Composable
import com.kmp.vayone.navigation.Screen

@Composable
fun CertKycScreen(
    isCert: Boolean = false,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
    ) {
}