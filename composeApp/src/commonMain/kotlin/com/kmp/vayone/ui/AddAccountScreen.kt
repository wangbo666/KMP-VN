package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.PayChannelBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.BankSelection
import com.kmp.vayone.ui.widget.InfoInputText
import com.kmp.vayone.ui.widget.InfoText
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.rememberKeyboardVisible
import com.kmp.vayone.viewmodel.CertViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_7E7B79
import theme.C_FC7700
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.bank_cert

@Composable
fun AddAccountScreen(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val certViewModel = remember { CertViewModel() }
    val isLoading by certViewModel.isLoading.collectAsState()
    var bankText by remember { mutableStateOf("") }
    var isBankError by remember { mutableStateOf(false) }
    var holderNameText by remember { mutableStateOf("") }
    var isHolderNameError by remember { mutableStateOf(false) }
    var bankAccountText by remember { mutableStateOf("") }
    var isBankAccountError by remember { mutableStateOf(false) }
    var confirmBankAccountText by remember { mutableStateOf("") }
    var isConfirmBankAccountError by remember { mutableStateOf(false) }
    var bankInfo by remember { mutableStateOf<PayChannelBean?>(null) }
    var showBankSheet by remember { mutableStateOf(false) }

    val isKeyboardVisible = rememberKeyboardVisible()

    LaunchedEffect(Unit) {
        certViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    LaunchedEffect(Unit) {
        certViewModel.getPersonalInfo()
    }
    LaunchedEffect(Unit) {
        certViewModel.personalInfoResult.collect {
            holderNameText = it?.firstName ?: ""
        }
    }
    LaunchedEffect(Unit) {
        certViewModel.addAccountResult.collect {
            toast(true, Strings["toast_add_account_receivable"])
            onBack()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().background(white).statusBarsPadding().imePadding(),
        topBar = {
            TopBar(
                Strings["add_account_receivable"]
            ) {
                onBack()
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().background(white)) {
                if(!isKeyboardVisible) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = Strings["contact_tips"],
                        fontSize = 12.sp,
                        color = C_7E7B79,
                        lineHeight = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                }
                Text(
                    text = Strings["next"],
                    color = white,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 48.sp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    C_FC7700,
                                    C_FC7700
                                ),
                            ), RoundedCornerShape(30.dp)
                        ).clickable {
                            if (bankText.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(1)
                                }
                                isBankError = true
                                return@clickable
                            }
                            if (holderNameText.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(2)
                                }
                                isHolderNameError = true
                                return@clickable
                            }
                            if (bankAccountText.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(3)
                                }
                                isBankAccountError = true
                                return@clickable
                            }
                            if (confirmBankAccountText.isBlank() || confirmBankAccountText != bankAccountText) {
                                scope.launch {
                                    listState.animateScrollToItem(4)
                                }
                                isConfirmBankAccountError = true
                                return@clickable
                            }
                            certViewModel.addAccount(
                                ParamBean(
                                    bankId = bankInfo?.id.toString(),
                                    accountUser = holderNameText,
                                    bankNo = bankAccountText,
                                )
                            )
                        })
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(white).padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus(force = true)
                    })
                }, state = listState
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(44.dp).background(C_FFF4E6)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.bank_cert),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = Strings["bank_account"],
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            color = C_FC7700,
                            modifier = Modifier.padding(start = 6.dp).weight(1f)
                        )
                    }
                }
            }
            item {
                InfoText(
                    title = Strings["bank"],
                    content = bankText,
                    hintText = Strings["please_choose_bank"],
                    errorText = Strings["please_choose_current_info"],
                    isError = isBankError,
                    isEnable = true,
                ) {
                    showBankSheet = true
                }
            }
            item {
                InfoInputText(
                    title = Strings["cardholder_name"],
                    content = holderNameText,
                    hintText = Strings["please_enter_holder_name"],
                    errorText = Strings["please_enter_holder_name"],
                    isError = isHolderNameError,
                ) {
                    isHolderNameError = false
                    holderNameText = it
                }
            }
            item {
                InfoInputText(
                    title = Strings["bank_account"],
                    content = bankAccountText,
                    hintText = Strings["please_enter_bank_account"],
                    errorText = Strings["please_enter_bank_account"],
                    isError = isBankAccountError,
                    keyboardType = KeyboardType.Number,
                ) {
                    isBankAccountError = false
                    bankAccountText = it
                    if (bankAccountText == confirmBankAccountText) {
                        isConfirmBankAccountError = false
                    }
                }
            }
            item {
                InfoInputText(
                    title = Strings["bank_account"],
                    content = confirmBankAccountText,
                    hintText = Strings["please_enter_bank_account"],
                    errorText = Strings["bank_account_not_match"],
                    isError = isConfirmBankAccountError,
                    keyboardType = KeyboardType.Number,
                ) {
                    isConfirmBankAccountError = false
                    if (bankAccountText == confirmBankAccountText) {
                        isBankAccountError = false
                    }
                    confirmBankAccountText = it
                }
            }
        }
    }
    if (showBankSheet) {
        BankSelection({ showBankSheet = false }) {
            isBankError = false
            bankText = it.bankName ?: ""
            bankInfo = it
        }
    }
    LoadingDialog(isLoading)
}

@Preview
@Composable
fun PreAddAccount() {
    AddAccountScreen(onBack = {}) {}
}