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
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.PayChannelBean
import com.kmp.vayone.data.RelativesBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.data.WorkInfoEnumBean
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.openSystemPermissionSettings
import com.kmp.vayone.postAllPermissions
import com.kmp.vayone.ui.widget.BankSelection
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.InfoInputText
import com.kmp.vayone.ui.widget.InfoText
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.ui.widget.WheelBottomSheet
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
    var showPermissionGuideDialog by remember { mutableStateOf(false) }
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

    var showBankSheet by remember { mutableStateOf(false) }
    var showContact1Sheet by remember { mutableStateOf(false) }
    var showContact2Sheet by remember { mutableStateOf(false) }
    var bankInfo by remember { mutableStateOf<PayChannelBean?>(null) }
    val workEnum by certViewModel.workEnumResult.collectAsState()
    var relativesStatus by remember { mutableStateOf<Int?>(null) }
    var friendStatus by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        certViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    LaunchedEffect(Unit) {
        if (!isCert) {
            certViewModel.getPersonalInfo()
        }
    }
    LaunchedEffect(Unit) {
        certViewModel.getContactInfo()
    }
    LaunchedEffect(Unit) {
        certViewModel.personalInfoResult.collect {
            holderNameText = it?.firstName ?: ""
        }
    }
    LaunchedEffect(Unit) {
        certViewModel.submitBankResult.collect {
            toast(true, Strings["submit_success"])
            onBack()
        }
    }
    LaunchedEffect(Unit) {
        certViewModel.contactInfo.collect {
            it?.let {
                contact1Text = it.relativesStr ?: ""
                contactPhone1Text = it.relativesMobile ?: ""
                contactName1Text = it.relativesName ?: ""
                contact2Text = it.otherRelativesStr ?: ""
                contactPhone2Text = it.otherMobile ?: ""
                contactName2Text = it.otherName ?: ""
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().background(white).statusBarsPadding().imePadding(),
        topBar = {
            TopBar(
                if (isCert) Strings["contact_info"] else Strings["bank_and_contact"]
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
                            if (contact1Text.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(6)
                                }
                                isContact1Error = true
                                return@clickable
                            }
                            if (contactName1Text.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(7)
                                }
                                isContactName1Error = true
                                return@clickable
                            }
                            if (contactPhone1Text.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(8)
                                }
                                isContactPhone1Error = true
                                return@clickable
                            }
                            if (contact2Text.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(9)
                                }
                                isContact2Error = true
                                return@clickable
                            }
                            if (contactName2Text.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(10)
                                }
                                isContactName2Error = true
                                return@clickable
                            }
                            if (contactPhone2Text.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(11)
                                }
                                isContactPhone2Error = true
                                return@clickable
                            }
                            scope.launch {
                                postAllPermissions(refuseAction = { isNever, permissions ->
                                    if (isNever) {
                                        permissionText =
                                            permissions.joinToString { it.permissionToString() }
                                        showPermissionGuideDialog = true
                                    }
                                }) {
                                    certViewModel.submitContactBank(
                                        ParamBean(
                                            bankInfoId = bankInfo?.countryId.toString(),
                                            bankId = bankInfo?.id.toString(),
                                            accountUser = holderNameText,
                                            bankNo = bankAccountText,
                                            bankCode = bankInfo?.bankCode,
                                            bankName = bankInfo?.bankName,
                                            relativesInfoVOList = arrayListOf(
                                                RelativesBean(
                                                    relativesStatus,
                                                    contactName1Text,
                                                    contactPhone1Text
                                                ),
                                                RelativesBean(
                                                    friendStatus,
                                                    contactName2Text,
                                                    contactPhone2Text
                                                )
                                            )
                                        )
                                    )
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
            loadingState,
            Modifier.background(white).fillMaxSize().padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus(force = true)
                    })
                },
            onRetry = {
                certViewModel.getContactInfo()
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                if (!isCert) {
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
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 8.dp
                                    ),
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
                        certViewModel.getWorkEnums()
                        showContact1Sheet = true
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
                        certViewModel.getWorkEnums()
                        showContact2Sheet = true
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
            if (showBankSheet) {
                BankSelection({ showBankSheet = false }) {
                    isBankError = false
                    bankText = it.bankName ?: ""
                    bankInfo = it
                }
            }
            if (showContact1Sheet && workEnum?.relatives != null) {
                WheelBottomSheet(
                    items = workEnum?.relatives ?: listOf(),
                    initialIndex = workEnum?.relatives?.indexOfFirst { it1 -> it1.state == relativesStatus }
                        ?: 0,
                    onDismiss = { showContact1Sheet = false }
                ) { it1 ->
                    contact1Text = it1.info
                    isContact1Error = false
                    relativesStatus = it1.state
                }
            }
            if (showContact2Sheet && workEnum?.otherRelatives != null) {
                WheelBottomSheet(
                    items = workEnum?.otherRelatives ?: listOf(),
                    initialIndex = workEnum?.otherRelatives?.indexOfFirst { it1 -> it1.state == friendStatus }
                        ?: 0,
                    onDismiss = { showContact2Sheet = false }
                ) { it1 ->
                    contact2Text = it1.info
                    isContact2Error = false
                    friendStatus = it1.state
                }
            }
            ConfirmDialog(
                showPermissionGuideDialog,
                title = Strings["dialog_permission_title"].format(permissionText),
                content = "",
                cancel = Strings["closed"],
                confirm = Strings["sure"],
                confirmAction = {
                    openSystemPermissionSettings()
                }
            ) {
                showPermissionGuideDialog = false
            }
        }
    }
}

@Preview
@Composable
fun PreBankCert() {
    CertBankScreen(onBack = {}) {}
}