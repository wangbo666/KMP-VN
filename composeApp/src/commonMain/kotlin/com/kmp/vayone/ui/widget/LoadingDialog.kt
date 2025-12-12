package com.kmp.vayone.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kmp.vayone.data.Strings
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_FC7700
import theme.white

@Composable
fun LoadingDialog(
    show: Boolean,
    onDismiss: () -> Unit = {}
) {
    if (!show) return

    // Dialog 可以拦截点击事件，也可以不拦截
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .size(100.dp)
                .background(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x80000000)
                ) // 半透明遮罩
                .clickable(enabled = false) {},
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(35.dp).align(Alignment.CenterHorizontally),
                color = C_FC7700 // 橙色
            )
            Text(
                text = Strings["loading"],
                fontSize = 15.sp,
                lineHeight = 15.sp,
                color = white,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PreLoading() {
    LoadingDialog(show = true) {}
}

