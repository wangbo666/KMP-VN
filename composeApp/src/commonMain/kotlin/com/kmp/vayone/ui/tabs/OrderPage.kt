package com.kmp.vayone.ui.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun OrderPage(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
) {
    Text("Order")
}