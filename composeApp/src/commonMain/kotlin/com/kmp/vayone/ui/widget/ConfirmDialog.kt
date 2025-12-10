package com.kmp.vayone.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.Strings
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_7E7B79
import theme.C_FC7700
import theme.C_FFF4E6
import theme.white

@Composable
fun ConfirmDialog(
    show: Boolean,
    title: String = Strings["set_password"],
    content: String = Strings["set_password_tips"],
    cancel: String = Strings["cancel"],
    confirm: String = Strings["confirm"],
    highLight: String = "XXXXXXXXXXX",
    cancelAction: () -> Unit = {},
    confirmAction: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    if (!show) return

    // Dialog 可以拦截点击事件，也可以不拦截
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    shape = RoundedCornerShape(16.dp),
                    color = white
                )
                .padding(top = 28.dp, bottom = 19.dp, start = 16.dp, end = 16.dp)
        ) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    color = C_2B2621,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }
            if (content.isNotBlank()) {
                MultiColoredText(
                    content,
                    listOf(
                        ColoredTextPart(highLight, C_2B2621, 14.sp, fontWeight = FontWeight.Bold) {
                        },
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f)
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(color = C_FFF4E6, shape = RoundedCornerShape(30.dp))
                        .clickable {
                            onDismiss()
                            cancelAction()
                        },
                    text = cancel,
                    fontSize = 16.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = C_FC7700,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.weight(1f)
                        .padding(start = 6.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(color = C_FC7700)
                        .clickable {
                            onDismiss()
                            confirmAction()
                        },
                    lineHeight = 44.sp,
                    text = confirm,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = white,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun PreConfirmDialog() {
    ConfirmDialog(show = true) {}
}