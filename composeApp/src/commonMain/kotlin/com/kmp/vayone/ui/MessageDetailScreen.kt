package com.kmp.vayone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.MessageBean
import com.kmp.vayone.ui.widget.TopBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_B4B0AD
import theme.C_F5F5F5
import theme.white

@Composable
fun MessageDetailScreen(
    message: MessageBean,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(white).statusBarsPadding()) {
        TopBar(message.theme ?: "") {
            onBack()
        }
        Text(
            text = message.content ?: "",
            color = C_B4B0AD,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .background(C_F5F5F5, RoundedCornerShape(8.dp))
                .padding(16.dp)
                .heightIn(min = 226.dp),
        )
    }
}

@Preview
@Composable
fun PreMessageDetail() {
    MessageDetailScreen(MessageBean(theme = "Theme", content = "COntent")) {

    }
}