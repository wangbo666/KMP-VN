package com.kmp.vayone.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kmp.vayone.data.PayChannelBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.viewmodel.CertViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_B4B0AD
import theme.C_E3E0DD
import theme.C_ED190E
import theme.C_F9F9F9
import theme.C_FC7700
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.bank_icon
import vayone.composeapp.generated.resources.bank_search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankSelection(
    onDismiss: () -> Unit,
    onConfirm: (PayChannelBean) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var content by remember { mutableStateOf("") }
    val certViewModel = remember { CertViewModel() }
    val loadingState by certViewModel.loadingState.collectAsState()
    val bankList by certViewModel.payChannelList.collectAsState()

    LaunchedEffect(Unit) {
        certViewModel.getPayChannelList()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white,
        scrimColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 606.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = Strings["choose_bank"],
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                color = C_FC7700,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .background(color = C_F9F9F9, RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = C_E3E0DD,
                        RoundedCornerShape(8.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    modifier = Modifier.weight(1f),
                    value = content,
                    onValueChange = { content = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        color = C_524F4C
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (content.isEmpty()) {
                                Text(
                                    text = Strings["search"],
                                    fontSize = 12.sp,
                                    color = C_B4B0AD
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Image(
                    painter = painterResource(Res.drawable.bank_search),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp).size(24.dp)
                )
            }
            LoadingBox(
                loadingState,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                onRetry = {
                    certViewModel.getPayChannelList()
                }) {
                val newList = if (content.isBlank()) bankList
                else bankList?.filter {
                    it.bankCode?.contains(content) == true
                            || it.bankName?.contains(content) == true
                }
                if (newList.isNullOrEmpty()) {
                    Text(
                        text = Strings["empty_search"],
                        fontSize = 12.sp,
                        color = C_B4B0AD,
                        lineHeight = 400.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(newList.size, key = { it }) {
                            PreBankSelection(newList[it]) { item ->
                                onConfirm(item)
                                onDismiss()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreBankSelection(item: PayChannelBean, onConfirm: (PayChannelBean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(47.dp)
            .clickable {
                onConfirm(item)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        KmpAsyncImage(item.logoUrl, modifier = Modifier.size(32.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(
                text = item.longCode ?: "",
                fontSize = 13.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Bold,
                color = C_524F4C,
            )
            Text(
                text = item.bankName ?: "",
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                color = C_B4B0AD,
            )
        }
    }
}

@Composable
fun KmpAsyncImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale
    )
}
