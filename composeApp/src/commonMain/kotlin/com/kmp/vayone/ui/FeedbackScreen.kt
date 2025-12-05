package com.kmp.vayone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.ui.widget.ToastHost
import com.kmp.vayone.ui.widget.TopBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_B4B0AD
import theme.C_F5F5F5
import theme.C_FC7700
import theme.white


@Composable
fun FeedbackScreen(
    toast: (show: Boolean, message: String) -> Unit,
    onBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding(), topBar = {
        TopBar(Strings["feedback"]) {
            onBack()
        }
    }) {
        var text by remember { mutableStateOf("") }
        Box(
            modifier = Modifier.fillMaxSize().background(white).padding(it)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TextField(
                    value = text,
                    onValueChange = { it1 -> text = it1 },
                    placeholder = {
                        Text(
                            text = Strings["enter_feedback"],
                            color = C_B4B0AD,
                            fontSize = 13.sp
                        )
                    },
                    textStyle = TextStyle(
                        color = C_2B2621,
                        fontSize = 13.sp
                    ),
                    singleLine = false,
                    shape = RoundedCornerShape(12.dp),   // ★ 圆角写这里
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .height(226.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = C_F5F5F5,
                        focusedContainerColor = C_F5F5F5
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // ⬅️ 点右下角 Done 时执行的逻辑
                            keyboardController?.hide()
                        }
                    ),
                )
                Box(
                    Modifier.padding(top = 10.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(C_FC7700).clickable {
                            if (text.isBlank()) {
                                val toastMessage = Strings["enter_feedback"]
                                val showToast = true
                                toast(showToast, toastMessage)
                            } else {
                                val toastMessage = Strings["feedback_success"]
                                val showToast = true
                                toast(showToast, toastMessage)
                                onBack()
                            }
                        }
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = Strings["submit"],
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = white,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreFeedback() {
    FeedbackScreen({ _, _ -> }) {}
}