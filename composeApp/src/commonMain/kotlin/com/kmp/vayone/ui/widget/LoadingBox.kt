package com.kmp.vayone.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_B4B0AD
import theme.C_FC7700
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.empty_net

// 定义状态
sealed class UiState {
    object Loading : UiState()
    data class Error(val message: String = Strings["net_error"]) : UiState()
    object Success : UiState()
}

@Composable
fun LoadingBox(
    state: UiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = C_FC7700
                    )
                }
            }

            is UiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = true) { onRetry.invoke() }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.empty_net),
                        contentDescription = null,
                        modifier = Modifier.size(155.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = state.message,
                        color = C_B4B0AD,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)
                    )

                }
            }

            is UiState.Success -> {
                content()
            }
        }
    }
}

@Preview
@Composable
fun PreLoadingBox() {
    LoadingBox(state = UiState.Loading) {

    }
}