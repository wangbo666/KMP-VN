package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.MessageBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_B4B0AD
import theme.C_ED190E
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.empty_message
import vayone.composeapp.generated.resources.empty_product
import vayone.composeapp.generated.resources.message_icon

@Composable
fun MessageScreen(
    onBack: () -> Unit,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    navigate: (Screen) -> Unit,
) {
    val viewModel = remember { MainViewModel() }
    val messageList by viewModel.messageList.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMessageList()
    }
    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }

    Scaffold(modifier = Modifier.statusBarsPadding(), topBar = {
        TopBar(Strings["message"]) {
            onBack()
        }
    }) { paddingValues ->
        LoadingBox(
            loadingState, modifier = Modifier.background(white)
                .padding(paddingValues), onRetry = {
                viewModel.getMessageList()
            }
        ) {
            if (messageList.isNullOrEmpty()) {
                EmptyMessage()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {

                        messageList?.forEach {
                            MessageItem(it) {
                                navigate(Screen.MessageDetail(it))
                                viewModel.markMessagesRead(listOf(it.id ?: 0))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    item: MessageBean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable {
                onClick()
            }.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            Image(
                painter = painterResource(Res.drawable.message_icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            if (item.readStatus == 0) {
                Spacer(
                    modifier = Modifier.size(12.dp).background(
                        C_ED190E,
                        RoundedCornerShape(6.dp)
                    ).align(Alignment.TopEnd)
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = item.theme ?: "",
                fontSize = 14.sp,
                color = C_2B2621,
                lineHeight = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.content ?: "",
                fontSize = 12.sp,
                color = C_B4B0AD,
                lineHeight = 14.sp,
            )
        }
        Text(
            text = item.createTime ?: "",
            fontSize = 12.sp,
            color = C_B4B0AD,
            lineHeight = 14.sp,
        )
    }
}

@Composable
fun EmptyMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_message),
            contentDescription = null,
            modifier = Modifier.size(155.dp).align(Alignment.CenterHorizontally)
        )
        Text(
            text = Strings["empty_message"],
            fontSize = 14.sp,
            color = C_B4B0AD,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 9.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun PreMessage() {
    EmptyMessage()
}