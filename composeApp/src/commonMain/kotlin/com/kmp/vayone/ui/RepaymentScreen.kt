package com.kmp.vayone.ui

import androidx.compose.runtime.Composable
import com.kmp.vayone.navigation.Screen

@Composable
fun RepaymentScreen(
    orderId: String?,
    orderNo: String?,
    amount: String?,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {

}