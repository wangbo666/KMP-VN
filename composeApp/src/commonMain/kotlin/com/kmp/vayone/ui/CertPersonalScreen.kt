@file:OptIn(ExperimentalTime::class)

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
import androidx.compose.foundation.relocation.BringIntoViewRequester
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
import com.kmp.vayone.data.Strings
import com.kmp.vayone.mobileType
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.openSystemPermissionSettings
import com.kmp.vayone.postAllPermissions
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.DatePickerBottomSheet
import com.kmp.vayone.ui.widget.InfoInputText
import com.kmp.vayone.ui.widget.InfoText
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.ui.widget.WheelBottomSheet
import com.kmp.vayone.util.convertDMYToYMD
import com.kmp.vayone.util.convertYMDToDMY
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isOver18
import com.kmp.vayone.util.isValidIDCard
import com.kmp.vayone.util.jumpCert
import com.kmp.vayone.util.log
import com.kmp.vayone.util.permissionToString
import com.kmp.vayone.util.toddMMyyyy
import com.kmp.vayone.viewmodel.CertViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.kyc_card
import vayone.composeapp.generated.resources.mine_select
import kotlin.math.max
import kotlin.time.ExperimentalTime

@Composable
fun CertPersonalScreen(
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
    var nameText by remember { mutableStateOf("") }
    var genderText by remember { mutableStateOf("") }
    var birthText by remember { mutableStateOf("") }
    var idNumText by remember { mutableStateOf("") }
    var educationText by remember { mutableStateOf("") }
    var monthlyText by remember { mutableStateOf("") }
    var marryText by remember { mutableStateOf("") }
    var zaloText by remember { mutableStateOf("") }
    var provinceText by remember { mutableStateOf("") }
    var addressText by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }
    var isGenderError by remember { mutableStateOf(false) }
    var isBirthError by remember { mutableStateOf(false) }
    var isIDError by remember { mutableStateOf(false) }
    var isEducationError by remember { mutableStateOf(false) }
    var isMonthlyError by remember { mutableStateOf(false) }
    var isMarryError by remember { mutableStateOf(false) }
    var isZaloError by remember { mutableStateOf(false) }
    var isProvinceError by remember { mutableStateOf(false) }
    var isAddressError by remember { mutableStateOf(false) }
    val nameRequester = remember { BringIntoViewRequester() }
    val idRequester = remember { BringIntoViewRequester() }
    val monthRequester = remember { BringIntoViewRequester() }
    val zaloRequester = remember { BringIntoViewRequester() }
    val addressRequester = remember { BringIntoViewRequester() }
    var showGenderSheet by remember { mutableStateOf(false) }
    var showEducationSheet by remember { mutableStateOf(false) }
    var showMarrySheet by remember { mutableStateOf(false) }
    val personalEnumResult by certViewModel.personalEnumResult.collectAsState()
    var genderStatus by remember { mutableStateOf<Int?>(null) }
    var educationStatus by remember { mutableStateOf<Int?>(null) }
    var marryStatus by remember { mutableStateOf<Int?>(null) }
    var provinceId by remember { mutableStateOf<Long?>(null) }
    var cityId by remember { mutableStateOf<Long?>(null) }
    var regionId by remember { mutableStateOf<Long?>(null) }

    var showPermissionGuideDialog by remember { mutableStateOf(false) }
    var permissionText by mutableStateOf("")
    var showBirthSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        certViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    LaunchedEffect(Unit) {
        certViewModel.getPersonalInfo()
    }
    LaunchedEffect(Unit) {
        certViewModel.personalSubmitResult.collect {
            it?.jumpCert(navigate)
        }
    }
    LaunchedEffect(certViewModel.personalInfoResult) {
        certViewModel.personalInfoResult.collect {
            it?.let {
                nameText = it.firstName ?: ""
                idNumText = it.cardNo ?: ""
                genderText = it.sexStr ?: ""
                birthText = it.birthDateStr?.convertYMDToDMY() ?: ""
                educationText = it.educationStr ?: ""
                monthlyText = if (it.salary == null) "" else it.salary.toString()
                marryText = it.marryStateStr ?: ""
                zaloText = it.zaloAccount ?: ""
                provinceText = "${it.provinceStr ?: ""}${it.cityStr ?: ""}${it.regionStr ?: ""}"
                addressText = it.currentAddress ?: ""
                genderStatus = it.sex
                educationStatus = it.education
                marryStatus = it.marryState
                provinceId = it.province
                cityId = it.city
                regionId = it.region
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(white).statusBarsPadding().imePadding(),
        topBar = {
            TopBar(
                Strings["personal_info"],
                rightText = if (isCert) "" else "${getAuthConfigList().indexOf("ID") + 1}/${getAuthConfigList().filterNot { it == "BANK" }.size}"
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
                            if (nameText.isBlank()) {
                                isNameError = true
                                scope.launch {
                                    listState.animateScrollToItem(1)
                                }
                                return@clickable
                            }
                            if (genderText.isBlank()) {
                                isGenderError = true
                                scope.launch {
                                    listState.animateScrollToItem(2)
                                }
                                return@clickable
                            }
                            if (birthText.isBlank() || !birthText.isOver18()) {
                                isBirthError = true
                                toast(true, Strings["under_18"])
                                scope.launch {
                                    listState.animateScrollToItem(3)
                                }
                                return@clickable
                            }
                            if (idNumText.isBlank() || idNumText.isValidIDCard()) {
                                isIDError = true
                                toast(true, Strings["id_number_error"])
                                scope.launch {
                                    listState.animateScrollToItem(4)
                                }
                                return@clickable
                            }
                            if (educationText.isBlank()) {
                                isEducationError = true
                                scope.launch {
                                    listState.animateScrollToItem(5)
                                }
                                return@clickable
                            }
                            if (monthlyText.isBlank()) {
                                isMonthlyError = true
                                scope.launch {
                                    listState.animateScrollToItem(6)
                                }
                                return@clickable
                            }
                            if (marryText.isBlank()) {
                                isMarryError = true
                                scope.launch {
                                    listState.animateScrollToItem(7)
                                }
                                return@clickable
                            }
                            if (zaloText.isBlank()) {
                                isZaloError = true
                                scope.launch {
                                    listState.animateScrollToItem(8)
                                }
                                return@clickable
                            }
                            if (provinceId == null || cityId == null || regionId == null) {
                                isProvinceError = true
                                scope.launch {
                                    listState.animateScrollToItem(9)
                                }
                                return@clickable
                            }
                            if (addressText.isBlank()) {
                                isAddressError = true
                                scope.launch {
                                    listState.animateScrollToItem(10)
                                }
                                return@clickable
                            }
                            scope.launch {
                                postAllPermissions(refuseAction = { isNever, permissions ->
                                    if (isNever) {
                                        permissionText =
                                            permissions.joinToString { it.permissionToString() }
                                        isShowConfirmExitDialog = true
                                    }
                                }) {
                                    certViewModel.submitPersonal(
                                        ParamBean(
                                            education = educationStatus.toString(),
                                            sex = genderStatus.toString(),
                                            marryState = marryStatus.toString(),
                                            userName = nameText,
                                            cardNo = idNumText,
                                            birthDate = birthText.convertDMYToYMD(),
                                            province = provinceId.toString(),
                                            address = addressText,
                                            region = regionId.toString(),
                                            city = cityId.toString(),
                                            zaloAccount = zaloText,
                                            salary = monthlyText
//                                userCommunicationRecordStr = Gson().toJson(getCallLog()).toBase64()
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
                certViewModel.getPersonalInfo()
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(44.dp).background(C_FFF4E6)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.mine_select),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = Strings["personal"],
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
                        title = Strings["name"],
                        content = nameText,
                        hintText = Strings["please_enter_name"],
                        errorText = Strings["please_enter_current_info"],
                        bringIntoViewRequester = nameRequester,
                        isError = isNameError
                    ) {
                        nameText = it
                        isNameError = false
                    }
                }
                item {
                    InfoText(
                        title = Strings["gender"],
                        content = genderText,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isGenderError,
                        isEnable = !isCert,
                    ) {
                        showGenderSheet = true
                        certViewModel.getEnums()
                        scope.launch {
                            listState.animateScrollToItem(2)
                        }
                    }
                }
                item {
                    InfoText(
                        title = Strings["date_of_birth"],
                        content = birthText,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isBirthError,
                        isEnable = !isCert,
                    ) {
                        showBirthSheet = true
                        scope.launch {
                            listState.animateScrollToItem(3)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["id_card_number"],
                        content = idNumText,
                        hintText = Strings["please_enter_id_card"],
                        errorText = Strings["please_enter_id_card"],
                        bringIntoViewRequester = idRequester,
                        isError = isIDError,
                        keyboardType = KeyboardType.Number,
                    ) {
                        idNumText = it
                        isEducationError = false
                    }
                }
                item {
                    InfoText(
                        title = Strings["education"],
                        content = educationText,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isEducationError,
                        isEnable = !isCert,
                    ) {
                        showEducationSheet = true
                        certViewModel.getEnums()
                        scope.launch {
                            listState.animateScrollToItem(5)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["monthly_salary"],
                        content = monthlyText,
                        hintText = Strings["please_enter_monthly"],
                        errorText = Strings["please_enter_monthly"],
                        bringIntoViewRequester = monthRequester,
                        isError = isMonthlyError,
                        keyboardType = KeyboardType.Phone,
                    ) { input ->
                        // 去掉前导零，但保留 "0" 本身
                        if (input.length > 1 && input.startsWith("0") && !input.startsWith("0.")) {
                            monthlyText = if (input.isEmpty()) "0"
                            else input.replaceFirst("^0+".toRegex(), "")
                        }
                        monthlyText = input
                        isMonthlyError = false
                    }
                }
                item {
                    InfoText(
                        title = Strings["marry_status"],
                        content = marryText,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isMarryError,
                        isEnable = !isCert,
                    ) {
                        showMarrySheet = true
                        certViewModel.getEnums()
                        scope.launch {
                            listState.animateScrollToItem(7)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["zalo"],
                        content = zaloText,
                        hintText = Strings["please_enter_zalo"],
                        errorText = Strings["please_enter_zalo"],
                        isError = isZaloError,
                        bringIntoViewRequester = zaloRequester,
                    ) {
                        zaloText = it
                        isZaloError = false
                    }
                }
                item {
                    InfoText(
                        title = Strings["province_city_town"],
                        content = provinceText,
                        hintText = Strings["please_choose"],
                        errorText = Strings["please_choose_current_info"],
                        isError = isProvinceError,
                        isEnable = !isCert,
                    ) {
                        scope.launch {
                            listState.animateScrollToItem(9)
                        }
                    }
                }
                item {
                    InfoInputText(
                        title = Strings["detail_address"],
                        content = addressText,
                        hintText = Strings["residential_address_hint"],
                        errorText = Strings["residential_address_hint"],
                        isError = isAddressError,
                        bringIntoViewRequester = addressRequester,
                    ) {
                        addressText = it
                        isAddressError = false
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            if (isShowConfirmExitDialog && !isCert) {
                val list = getAuthConfigList().filterNot { it1 -> it1.isBlank() || it1 == "BANK" }
                val step = list.size - max(0, list.indexOf("ID"))
                ConfirmDialog(
                    true,
                    title = "",
                    content = Strings["auth_exit_confirm"].format(step.toString()),
                    cancel = Strings["give_up"],
                    confirm = Strings["continue_str"],
                    highLight = step.toString(),
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
            if (showGenderSheet && personalEnumResult?.gender != null) {
                WheelBottomSheet(
                    items = personalEnumResult?.gender ?: listOf(),
                    initialIndex = personalEnumResult?.gender?.indexOfFirst { it1 -> it1.state == genderStatus }
                        ?: 0,
                    onDismiss = { showGenderSheet = false }
                ) { it1 ->
                    genderText = it1.info
                    showGenderSheet = false
                    isGenderError = false
                    genderStatus = it1.state
                }
            }
            if (showEducationSheet && personalEnumResult?.education != null) {
                WheelBottomSheet(
                    items = personalEnumResult?.education ?: listOf(),
                    initialIndex = personalEnumResult?.education?.indexOfFirst { it1 -> it1.state == educationStatus }
                        ?: 0,
                    onDismiss = { showEducationSheet = false }
                ) { it1 ->
                    educationText = it1.info
                    showEducationSheet = false
                    isEducationError = false
                    educationStatus = it1.state
                }
            }
            if (showMarrySheet && personalEnumResult?.maritalStatus != null) {
                WheelBottomSheet(
                    items = personalEnumResult?.maritalStatus ?: listOf(),
                    initialIndex = personalEnumResult?.maritalStatus?.indexOfFirst { it1 -> it1.state == marryStatus }
                        ?: 0,
                    onDismiss = { showMarrySheet = false }
                ) { it1 ->
                    marryText = it1.info
                    showMarrySheet = false
                    isMarryError = false
                    marryStatus = it1.state
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
            if (showBirthSheet) {
                DatePickerBottomSheet(
                    onDismiss = { showBirthSheet = false },
                    onConfirm = { date ->
                        birthText = date.toString()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PrePersonal() {
    CertPersonalScreen(false, onBack = {}) {}
}

