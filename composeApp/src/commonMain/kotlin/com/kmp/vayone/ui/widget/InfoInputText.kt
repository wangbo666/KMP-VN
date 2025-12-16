package com.kmp.vayone.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_B4B0AD
import theme.C_E3E0DD
import theme.C_ED190E
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.personal_bottom

@Composable
fun InfoInputText(
    title: String = "",
    content: String = "",
    hintText: String = "",
    errorText: String = "",
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    bringIntoViewRequester: BringIntoViewRequester? = null,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    onValueChange: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 14.sp,
            color = C_524F4C,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                .background(color = Color.Transparent, RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = if (isError) C_ED190E else C_E3E0DD,
                    RoundedCornerShape(8.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                modifier = Modifier.onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        scope.launch {
                            bringIntoViewRequester?.bringIntoView()
                        }
                    }
                },
                value = content,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = if (isError) C_ED190E else C_524F4C
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Done
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (isError) C_ED190E else C_E3E0DD,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (content.isEmpty()) {
                            Text(
                                text = hintText,
                                fontSize = 12.sp,
                                color = C_B4B0AD
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
        if (isError) {
            Text(
                text = errorText,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 12.sp,
                color = C_ED190E,
            )
        }
    }
}

@Composable
fun InfoText(
    title: String = "Title",
    content: String = "Content",
    hintText: String = "Hint",
    errorText: String = "ErrorText",
    isError: Boolean = false,
    isEnable: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    onClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 14.sp,
            color = C_524F4C,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.Transparent, RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = if (isError) C_ED190E else C_E3E0DD,
                    RoundedCornerShape(8.dp)
                )
                .clickable(isEnable) {
                    onClick()
                }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = content.ifBlank { hintText },
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = if (content.isBlank()) C_B4B0AD else C_524F4C,
                fontWeight = FontWeight.Normal,
            )
            if (isEnable) {
                Image(
                    painter = painterResource(Res.drawable.personal_bottom),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        if (isError) {
            Text(
                text = errorText,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 12.sp,
                color = C_ED190E,
            )
        }
    }
}


@Preview
@Composable
fun PreInfoInput() {
    InfoInputText(isError = false) {}
}