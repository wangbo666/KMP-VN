package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.kmp.vayone.data.CacheManager.getAuthConfigList
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.postAllPermissions
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.InfoInputText
import com.kmp.vayone.ui.widget.InfoText
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.util.permissionToString
import com.kmp.vayone.viewmodel.CertViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_7E7B79
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.bank_cert
import vayone.composeapp.generated.resources.mine_select
import kotlin.math.max

@Composable
fun CertBankScreen(
    isCert: Boolean = false,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val certViewModel = remember { CertViewModel() }
    val isLoading by certViewModel.isLoading.collectAsState()
    val loadingState by certViewModel.loadingState.collectAsState()
    var isShowConfirmExitDialog by remember { mutableStateOf(false) }
    var permissionText by mutableStateOf("")
    var bankText by remember { mutableStateOf("") }
    var isBankError by remember { mutableStateOf(false) }
    var holderNameText by remember { mutableStateOf("") }
    var isHolderNameError by remember { mutableStateOf(false) }
    var bankAccountText by remember { mutableStateOf("") }
    var isBankAccountError by remember { mutableStateOf(false) }
    var confirmBankAccountText by remember { mutableStateOf("") }
    var isConfirmBankAccountError by remember { mutableStateOf(false) }
    var contact1Text by remember { mutableStateOf("") }
    var isContact1Error by remember { mutableStateOf(false) }
    var contactName1Text by remember { mutableStateOf("") }
    var isContactName1Error by remember { mutableStateOf(false) }
    var contactPhone1Text by remember { mutableStateOf("") }
    var isContactPhone1Error by remember { mutableStateOf(false) }
    var contact2Text by remember { mutableStateOf("") }
    var isContact2Error by remember { mutableStateOf(false) }
    var contactName2Text by remember { mutableStateOf("") }
    var isContactName2Error by remember { mutableStateOf(false) }
    var contactPhone2Text by remember { mutableStateOf("") }
    var isContactPhone2Error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        certViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().background(white).statusBarsPadding().imePadding(),
        topBar = {
            TopBar(
                Strings["bank_and_contact"]
            ) {
                if (isCert) {
                    onBack()
                } else {
                    isShowConfirmExitDialog = true
                }
            }
        },
        bottomBar = {
            if (!isCert) {
                Text(
                    text = Strings["next"],
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
                                    C_FEB201
                                ),
                            ), RoundedCornerShape(30.dp)
                        ).clickable {
                            scope.launch {
                                postAllPermissions(refuseAction = { isNever, permissions ->
                                    if (isNever) {
                                        permissionText =
                                            permissions.joinToString { it.permissionToString() }
                                        isShowConfirmExitDialog = true
                                    }
                                }) {
                                }
                            }
                        },
                    color = white,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 48.sp,
                )
            }
        }) { paddingValues ->
        LoadingBox(
            UiState.Success,
            Modifier.background(white).fillMaxSize().padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus(force = true)
                    })
                },
            onRetry = {
                certViewModel.getPersonalInfo()
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
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
                        if (!isCert) {
                            Text(
                                text = Strings["bank_tips"],
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                                color = C_7E7B79,
                                textAlign = TextAlign.Start
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
                        isEnable = !isCert,
                    ) {
                        isBankError = false
                        scope.launch {
                            listState.animateScrollToItem(2)
                        }
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
                        scope.launch {
                            listState.animateScrollToItem(3)
                        }
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
                        scope.launch {
                            listState.animateScrollToItem(4)
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
                        confirmBankAccountText = it
                        scope.launch {
                            listState.animateScrollToItem(5)
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 16.dp)
                                .height(44.dp).background(C_FFF4E6)
                                .padding(start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.bank_cert),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = Strings["contact_info"],
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 18.sp,
                                color = C_FC7700,
                                modifier = Modifier.padding(start = 6.dp).weight(1f)
                            )
                        }
                        if (!isCert) {
                            Text(
                                text = Strings["contact_tips"],
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                                color = C_7E7B79,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
                item {
                    InfoText(
                        title = Strings["contact_people1"],
                        content = contact1Text,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isContact1Error,
                        isEnable = !isCert,
                    ) {
                        isContact1Error = false
                        scope.launch {
                            listState.animateScrollToItem(7)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["contact_name"],
                        content = contactName1Text,
                        hintText = Strings["please_enter_contact_name"],
                        errorText = Strings["please_enter_contact_name"],
                        isError = isContactName1Error,
                    ) {
                        isContactName1Error = false
                        contactName1Text = it
                        scope.launch {
                            listState.animateScrollToItem(8)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["contact_phone_number"],
                        content = contactPhone1Text,
                        hintText = Strings["please_enter_bank_account"],
                        errorText = Strings["please_enter_bank_account"],
                        isError = isContactPhone1Error,
                        keyboardType = KeyboardType.Number,
                    ) {
                        isContactPhone1Error = false
                        contactPhone1Text = it
                        scope.launch {
                            listState.animateScrollToItem(9)
                        }
                    }
                }
                item {
                    InfoText(
                        title = Strings["contact_people2"],
                        content = contact2Text,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isContact2Error,
                        isEnable = !isCert,
                    ) {
                        isContact2Error = true
                        scope.launch {
                            listState.animateScrollToItem(10)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["contact_name"],
                        content = contactName2Text,
                        hintText = Strings["please_enter_contact_name"],
                        errorText = Strings["please_enter_contact_name"],
                        isError = isContactName2Error,
                    ) {
                        isContactName2Error = false
                        contactName2Text = it
                        scope.launch {
                            listState.animateScrollToItem(11)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["contact_phone_number"],
                        content = contactPhone2Text,
                        hintText = Strings["please_enter_phone_number"],
                        errorText = Strings["please_enter_phone_number"],
                        isError = isContactPhone2Error,
                        keyboardType = KeyboardType.Number,
                    ) {
                        isContactPhone2Error = false
                        contactPhone2Text = it
                        scope.launch {
                            listState.animateScrollToItem(12)
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (isShowConfirmExitDialog && !isCert) {
                val step = "1"
                ConfirmDialog(
                    true,
                    title = "",
                    content = Strings["auth_exit_confirm"].format(step),
                    cancel = Strings["give_up"],
                    confirm = Strings["continue_str"],
                    highLight = step,
                    cancelAction = {
                        onBack()
                    },
                    confirmAction = {
                    }
                ) {
                    isShowConfirmExitDialog = false
                }
            }
            LoadingDialog(isLoading)
        }
    }
}

@Preview
@Composable
fun PreBankCert() {
    CertBankScreen(onBack = {}) {}
}