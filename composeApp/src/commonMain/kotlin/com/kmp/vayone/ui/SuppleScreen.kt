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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.InfoInputText
import com.kmp.vayone.ui.widget.InfoText
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.ui.widget.WheelBottomSheet
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isValidEmail
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
import vayone.composeapp.generated.resources.supply_company
import vayone.composeapp.generated.resources.supply_other

@Composable
fun SuppleScreen(
    isCert: Boolean = false,
    amount: String,
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
    var reasonText by remember { mutableStateOf("") }
    var isReasonError by remember { mutableStateOf(false) }
    var showReasonSheet by remember { mutableStateOf(false) }
    var emailText by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var professionText by remember { mutableStateOf("") }
    var isProfessionError by remember { mutableStateOf(false) }
    var showProfessionSheet by remember { mutableStateOf(false) }
    var companyNameText by remember { mutableStateOf("") }
    var isCompanyNameError by remember { mutableStateOf(false) }
    var companyPhoneText by remember { mutableStateOf("") }
    var isCompanyPhoneError by remember { mutableStateOf(false) }
    var companyAddressText by remember { mutableStateOf("") }
    var isCompanyAddressError by remember { mutableStateOf(false) }
    var facebookText by remember { mutableStateOf("") }
    var isFacebookError by remember { mutableStateOf(false) }
    val reasonEnum by certViewModel.personalEnumResult.collectAsState()
    var reasonStatus by remember { mutableStateOf<Int?>(null) }
    val workEnum by certViewModel.workEnumResult.collectAsState()
    var companyStatus by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        launch {
            certViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
        launch {
            if (isCert) {
                certViewModel.getPersonalInfo()
            }
        }
        launch {
            if (isCert) {
                certViewModel.getContactInfo()
            }
        }
        launch {
            certViewModel.personalInfoResult.collect {
                it?.let {
                    reasonText = it.purposeStr ?: ""
                    emailText = it.email ?: ""
                }
            }
        }
        launch {
            certViewModel.contactInfo.collect {
                it?.let {
                    facebookText = it.facebookUid ?: ""
                    professionText = it.industryStr ?: ""
                    companyStatus = it.jobNature
                    companyNameText = it.companyName ?: ""
                    companyPhoneText = it.companyTel ?: ""
                    companyAddressText = it.companyAddress ?: ""
                }
            }
        }
        launch {
            certViewModel.submitSuppleResult.collect {
                toast(true, Strings["submit_success"])
                onBack()
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().background(white).statusBarsPadding().imePadding(),
        topBar = {
            TopBar(
                Strings["supple_info"]
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
                                    C_FC7700
                                ),
                            ), RoundedCornerShape(30.dp)
                        ).clickable {
                            if (reasonText.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(1)
                                }
                                isReasonError = true
                                return@clickable
                            }
                            if (!emailText.isValidEmail()) {
                                scope.launch {
                                    listState.animateScrollToItem(2)
                                }
                                isEmailError = true
                                return@clickable
                            }
                            if (professionText.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(4)
                                }
                                isProfessionError = true
                                return@clickable
                            }
                            if (companyStatus != 19) {
                                if (companyNameText.isBlank()) {
                                    scope.launch {
                                        listState.animateScrollToItem(5)
                                    }
                                    isCompanyNameError = true
                                    return@clickable
                                }
                                if (companyPhoneText.isBlank()) {
                                    scope.launch {
                                        listState.animateScrollToItem(6)
                                    }
                                    isCompanyPhoneError = true
                                    return@clickable
                                }
                                if (companyAddressText.isBlank()) {
                                    scope.launch {
                                        listState.animateScrollToItem(7)
                                    }
                                    isCompanyAddressError = true
                                    return@clickable
                                }
                            }
                            if (facebookText.isBlank()) {
                                scope.launch {
                                    listState.animateScrollToItem(if (companyStatus != 19) 8 else 5)
                                }
                                isFacebookError = true
                                return@clickable
                            }
                            certViewModel.submitSupple(
                                ParamBean(
                                    purpose = reasonStatus.toString(),
                                    email = emailText,
                                    companyName = companyNameText,
                                    companyTel = companyPhoneText,
                                    companyAddress = companyAddressText,
                                    industry = companyStatus.toString(),
                                    facebookUid = facebookText,
                                )
                            )
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
                                text = Strings["supple_info"],
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 18.sp,
                                color = C_FC7700,
                                modifier = Modifier.padding(start = 6.dp).weight(1f)
                            )
                        }
                        if (!isCert) {
                            Text(
                                text = Strings["supple_info_tips"].replace(
                                    "2,000,000",
                                    amount.ifBlank { "2,000,000₫" }),
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
                        title = Strings["loan_purpose"],
                        content = reasonText,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isReasonError,
                        isEnable = !isCert,
                    ) {
                        showReasonSheet = true
                        certViewModel.getEnums()
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["email"],
                        content = emailText,
                        hintText = Strings["please_enter_email"],
                        errorText = Strings["please_enter_true_email"],
                        isError = isEmailError,
                    ) {
                        isEmailError = false
                        emailText = it
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(44.dp).background(C_FFF4E6)
                            .padding(start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.supply_company),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = Strings["company_info"],
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            color = C_FC7700,
                            modifier = Modifier.padding(start = 6.dp).weight(1f)
                        )
                    }
                }
                item {
                    InfoText(
                        title = Strings["profession"],
                        content = professionText,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isProfessionError,
                        isEnable = !isCert,
                    ) {
                        showProfessionSheet = true
                        certViewModel.getWorkEnums()
                    }
                }
                if (companyStatus != 19) {
                    item {
                        InfoInputText(
                            title = Strings["company_name"],
                            content = companyNameText,
                            hintText = Strings["please_enter_company_name"],
                            errorText = Strings["please_enter_company_name"],
                            isError = isCompanyNameError,
                        ) {
                            isCompanyNameError = false
                            companyNameText = it
                        }
                    }
                    item {
                        InfoInputText(
                            title = Strings["company_number"],
                            content = companyPhoneText,
                            hintText = Strings["please_enter_company_number"],
                            errorText = Strings["please_enter_company_number"],
                            isError = isCompanyPhoneError,
                        ) {
                            isCompanyPhoneError = false
                            companyPhoneText = it
                        }
                    }
                    item {
                        InfoInputText(
                            title = Strings["company_address"],
                            content = companyAddressText,
                            hintText = Strings["please_enter_company_address"],
                            errorText = Strings["please_enter_company_address"],
                            isError = isCompanyAddressError,
                        ) {
                            isCompanyAddressError = false
                            companyAddressText = it
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(44.dp).background(C_FFF4E6)
                            .padding(start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.supply_other),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = Strings["other_info"],
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            color = C_FC7700,
                            modifier = Modifier.padding(start = 6.dp).weight(1f)
                        )
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["facebook_uid"],
                        content = facebookText,
                        hintText = Strings["please_enter_facebook"],
                        errorText = Strings["please_enter_facebook"],
                        isError = isFacebookError,
                    ) {
                        isFacebookError = false
                        facebookText = it
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        LoadingDialog(isLoading)
        if (isShowConfirmExitDialog && !isCert) {
            val step = amount.ifBlank { "2,000,000₫" }
            ConfirmDialog(
                true,
                title = "",
                content = Strings["dialog_supple"].format(step),
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
        if (showReasonSheet && reasonEnum?.purpose != null) {
            WheelBottomSheet(
                items = reasonEnum?.purpose ?: listOf(),
                initialIndex = reasonEnum?.purpose?.indexOfFirst { it1 -> it1.state == reasonStatus }
                    ?: 0,
                onDismiss = { showReasonSheet = false }
            ) { it1 ->
                reasonText = it1.info
                isReasonError = false
                reasonStatus = it1.state
            }
        }
        if (showProfessionSheet && workEnum?.jobnature != null) {
            WheelBottomSheet(
                items = workEnum?.jobnature ?: listOf(),
                initialIndex = workEnum?.jobnature?.indexOfFirst { it1 -> it1.state == companyStatus }
                    ?: 0,
                onDismiss = { showProfessionSheet = false }
            ) { it1 ->
                professionText = it1.info
                isProfessionError = false
                companyStatus = it1.state
            }
        }
    }
}

@Preview
@Composable
fun PreSuppleScreen() {
    SuppleScreen(amount = "11111", onBack = {}) {

    }
}