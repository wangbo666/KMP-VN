package com.kmp.vayone.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmp.vayone.data.Strings
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ToastHost(message: String, show: Boolean = true, onDismiss: () -> Unit = {}) {
    if (show && message.isNotBlank()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(visible = show) {
                Text(
                    text = message,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            LaunchedEffect(message) {
                kotlinx.coroutines.delay(2000)
                onDismiss()
            }
        }
    }
}

@Preview
@Composable
fun PreToast() {
    ToastHost(Strings["privacy_toast_agree1"])
}