package com.kmp.vayone.ui

import androidx.compose.runtime.Composable
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.SignPageParams

@Composable
fun LoanResultScreen(
    params: SignPageParams,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
}