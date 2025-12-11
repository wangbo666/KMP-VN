package com.kmp.vayone.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_FC7700

@Composable
fun LoadingDialog(
    show: Boolean,
    onDismiss: () -> Unit = {}
) {
    if (!show) return

    // Dialog 可以拦截点击事件，也可以不拦截
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x80000000)
                ) // 半透明遮罩
                .clickable(enabled = false) {}, // 拦截点击事件,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(35.dp),
                color = C_FC7700 // 橙色
            )
        }
    }
}

@Preview
@Composable
fun PreLoading() {
    LoadingDialog(show = true) {}
}

