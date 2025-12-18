package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.vayone.data.BankCardBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.viewmodel.CertViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_B4B0AD
import theme.C_FC7700
import theme.C_FFBB48
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.accounts_add
import vayone.composeapp.generated.resources.accounts_card
import vayone.composeapp.generated.resources.accounts_default
import vayone.composeapp.generated.resources.empty_accounts

@Composable
fun AccountCenter(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val certViewModel = remember { CertViewModel() }
    val loadingState by certViewModel.loadingState.collectAsState()
    val orderList by certViewModel.accountList.collectAsState()
    var isShowDefaultDialog by remember { mutableStateOf<Long?>(null) }
    var isShowDeleteDialog by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        certViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                certViewModel.getBankCardList(false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(white)) {
        Column(
            modifier = Modifier.fillMaxWidth().background(
                brush = Brush.horizontalGradient(
                    listOf(
                        C_FC7700, C_FFBB48
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
        ) {
            TopBar(
                title = Strings["accounts"],
                tintColor = white,
                modifier = Modifier.statusBarsPadding().fillMaxWidth()
                    .height(44.dp)
            ) {
                onBack()
            }
            Text(
                text = Strings["accounts"],
                fontWeight = FontWeight.Bold,
                color = white,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 14.dp)
            )
            Text(
                text = Strings["account_receivable_tips"],
                fontWeight = FontWeight.Normal,
                color = white,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
            )
        }
        LoadingBox(
            loadingState, onRetry = {}, modifier = Modifier.weight(1f),
        ) {
            if (orderList.isEmpty()) {
                EmptyAccounts()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 10.dp)
                ) {
                    item {
                        orderList.forEach { item ->
                            AccountsCardItem(item, defaultAction = {
                                isShowDefaultDialog = item.id
                            }, deleteAction = {
                                isShowDeleteDialog = item.id
                            })
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                .background(white, RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.dp,
                                    color = C_FC7700, RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    navigate(Screen.AddAccount)
                                }
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.accounts_add),
                                contentDescription = null,
                                modifier = Modifier.padding(vertical = 12.dp).size(32.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = Strings["add_account"],
                                color = C_FC7700,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 18.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
            ConfirmDialog(
                isShowDefaultDialog != null,
                confirm = Strings["sure"],
                cancel = Strings["closed"],
                title = Strings["set_default_title"],
                content = Strings["set_default_desc"],
                cancelAction = {},
                confirmAction = {
                    certViewModel.setDefaultCard(isShowDefaultDialog.toString())
                }) {
                isShowDefaultDialog = null
            }
            ConfirmDialog(
                isShowDeleteDialog != null,
                confirm = Strings["sure"],
                cancel = Strings["closed"],
                title = Strings["unbind"],
                content = Strings["unbind_desc"],
                cancelAction = {},
                confirmAction = {
                    certViewModel.unBindCard(isShowDeleteDialog.toString())
                }) {
                isShowDeleteDialog = null
            }
        }
    }
}

@Composable
fun AccountsCardItem(
    item: BankCardBean,
    defaultAction: () -> Unit,
    deleteAction: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 10.dp)) {
        Image(
            painter = painterResource(Res.drawable.accounts_card),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize(),
            contentScale = ContentScale.FillBounds,
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.bankName,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                color = white,
                modifier = Modifier.padding(start = 33.dp, top = 28.dp)
            )
            Text(
                text = item.bankNo ?: "",
                fontSize = 20.sp,
                lineHeight = 20.sp,
                color = white,
                modifier = Modifier.padding(start = 33.dp, top = 5.dp, bottom = 13.dp)
            )
            if (item.isDefault == 1) {
                Spacer(modifier = Modifier.height(15.dp))
            }
            if (item.isDefault != 1) {
                Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                    Text(
                        text = Strings["default_str"],
                        color = white,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                            .background(white.copy(0.25f), RoundedCornerShape(bottomStart = 16.dp))
                            .clickable {
                                defaultAction()
                            },
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(0.5.dp).fillMaxHeight().background(white))
                    Text(
                        text = Strings["untie"],
                        color = white,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                            .background(white.copy(0.25f), RoundedCornerShape(bottomEnd = 16.dp))
                            .clickable {
                                deleteAction()
                            },
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        if (item.isDefault == 1) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(end = 33.dp, top = 43.dp)
            ) {
                Text(
                    text = Strings["default_str"],
                    fontSize = 13.sp,
                    color = white,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Image(
                    painter = painterResource(Res.drawable.accounts_default),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 12.dp).size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyAccounts() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_accounts),
            contentDescription = null,
            modifier = Modifier.size(155.dp).align(Alignment.CenterHorizontally)
        )
        Text(
            text = Strings["empty_bankcard"],
            fontSize = 12.sp,
            color = C_B4B0AD,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .padding(top = 9.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun PreAccounts() {

}