package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.getLastKnownLocation
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isValidPhoneNumber
import com.kmp.vayone.util.log
import com.kmp.vayone.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_524F4C
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_E3E0DD
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.dialog_close
import vayone.composeapp.generated.resources.dialog_customer
import vayone.composeapp.generated.resources.dialog_customer_bg
import vayone.composeapp.generated.resources.login_bg
import vayone.composeapp.generated.resources.login_customer
import vayone.composeapp.generated.resources.login_otp
import vayone.composeapp.generated.resources.login_password
import vayone.composeapp.generated.resources.login_phone
import vayone.composeapp.generated.resources.password_close
import vayone.composeapp.generated.resources.password_open
import vayone.composeapp.generated.resources.splash

@Composable
fun LoginScreen(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onNavigate: (Screen) -> Unit
) {
    // remember prevents recreating a new instance on each recomposition
    val loginViewModel = remember { LoginViewModel() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var loginType by remember { mutableStateOf(0) }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(0) }
    var isCounting by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var isHidePassword by remember { mutableStateOf(false) }

    val customerData by loginViewModel.customer.collectAsState()
    var isShowCustomerDialog by remember { mutableStateOf(false) }
    val isLoading by loginViewModel.isLoading.collectAsState()

    val scope = rememberCoroutineScope()
    // 每秒递减
    LaunchedEffect(key1 = isCounting) {
        if (isCounting) {
            timeLeft = 59
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // 结束后恢复状态
            isCounting = false
        }
    }
    LaunchedEffect(Unit) {
        launch {
            loginViewModel.sendOtpResult.collect {
                isCounting = true
            }
        }
        launch {
            loginViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
        launch {
            loginViewModel.loginResult.collect {
                CacheManager.setLoginInfo(it)
                CacheManager.setToken(it?.token ?: "")
                loginViewModel.postDeviceInfo()
            }
        }
        launch {
            loginViewModel.postDeviceResult.collect {
                if (CacheManager.getLoginInfo()?.passwdSign == 0) {
                    onNavigate(Screen.SetPassword)
                } else {
                    onNavigate(Screen.Home(0))
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus(force = true)
                })
            }
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.splash),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
        Image(
            modifier = Modifier.statusBarsPadding()
                .padding(top = 10.dp, end = 16.dp).size(24.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    isShowCustomerDialog = true
                    loginViewModel.getCustomer()
                },
            painter = painterResource(Res.drawable.login_customer),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.TopEnd
        )
        Text(
            text = Strings["app_name"],
            color = white,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.statusBarsPadding()
                .padding(top = 18.dp)
                .align(Alignment.TopCenter)
        )
        Image(
            modifier = Modifier.statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize().padding(top = 68.dp, end = 10.dp, bottom = 20.dp),
            painter = painterResource(Res.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
                .imePadding()
                .padding(top = 103.dp, end = 34.dp, bottom = 20.dp)
        ) {
            Text(
                text = Strings["login"],
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = C_2B2621,
                modifier = Modifier.padding(start = 16.dp, top = 25.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = Strings["otp_login"],
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (loginType == 0) white else C_7E7B79,
                    lineHeight = 56.sp,
                    modifier = Modifier.weight(1f).padding(end = 10.dp)
                        .align(Alignment.CenterVertically)
                        .background(
                            if (loginType == 0) C_FC7700 else C_FFF4E6,
                            RoundedCornerShape(12.dp)
                        ).clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (loginType != 0) {
                                loginType = 0
                            }
                        },
                )
                Text(
                    text = Strings["password_login"],
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (loginType == 0) C_7E7B79 else white,
                    lineHeight = 56.sp,
                    modifier = Modifier.weight(1f)
                        .align(Alignment.CenterVertically)
                        .background(
                            if (loginType == 0) C_FFF4E6 else C_FC7700,
                            RoundedCornerShape(12.dp)
                        ).clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (loginType != 1) {
                                loginType = 1
                            }
                        },
                )
            }
            Text(
                text = Strings["login_tip"],
                fontSize = 11.sp,
                color = C_524F4C,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 11.dp),
                textAlign = TextAlign.Start
            )
            Text(
                text = Strings["photo_number"],
                fontSize = 14.sp,
                color = C_524F4C,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 2.dp),
                textAlign = TextAlign.Start
            )
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(width = 1.dp, color = C_E3E0DD, RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(Res.drawable.login_phone),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 10.dp, top = 13.dp).size(24.dp),
                )
                Spacer(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 13.dp)
                        .size(1.dp, 24.dp)
                        .background(C_E3E0DD)
                )
                TextField(
                    value = phone,
                    onValueChange = { it1 -> phone = it1 },
                    placeholder = {
                    },
                    textStyle = TextStyle(
                        color = C_2B2621,
                        fontSize = 14.sp
                    ),
                    singleLine = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = white,
                        focusedContainerColor = white
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            if (loginType == 0) {
                Text(
                    text = Strings["otp_enter"],
                    fontSize = 14.sp,
                    color = C_524F4C,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 12.dp, top = 4.dp)
                        .height(50.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(width = 1.dp, color = C_E3E0DD, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.login_otp),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 10.dp, top = 13.dp).size(24.dp),
                        )
                        Spacer(
                            modifier = Modifier
                                .padding(start = 8.dp, top = 13.dp)
                                .size(1.dp, 24.dp)
                                .background(C_E3E0DD)
                        )
                        TextField(
                            value = otp,
                            onValueChange = { it1 -> otp = it1 },
                            placeholder = {
                            },
                            textStyle = TextStyle(
                                color = C_2B2621,
                                fontSize = 14.sp
                            ),
                            singleLine = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = white,
                                focusedContainerColor = white
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = if (isCounting) "$timeLeft" + "s" else Strings["get_str"],
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = white,
                        lineHeight = 50.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 12.dp).size(93.dp, 50.dp)
                            .background(
                                if (isCounting) C_B4B0AD else C_FC7700,
                                RoundedCornerShape(8.dp)
                            ).clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = !isCounting) {
                                if (!phone.isValidPhoneNumber()) {
                                    toast(true, Strings["please_check_phone"])
                                    return@clickable
                                }
                                loginViewModel.sendOTP(phone)
                            }
                    )
                }
            }
            if (loginType != 0) {
                Text(
                    text = Strings["password"],
                    fontSize = 14.sp,
                    color = C_524F4C,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 4.dp, start = 16.dp, end = 16.dp)
                        .border(width = 1.dp, color = C_E3E0DD, RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.login_password),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 10.dp, top = 13.dp).size(24.dp),
                    )
                    Spacer(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 13.dp)
                            .size(1.dp, 24.dp)
                            .background(C_E3E0DD)
                    )
                    TextField(
                        value = password,
                        onValueChange = { it1 -> password = it1 },
                        placeholder = {
                        },
                        textStyle = TextStyle(
                            color = C_2B2621,
                            fontSize = 14.sp
                        ),
                        visualTransformation = if (isHidePassword)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        singleLine = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = white,
                            focusedContainerColor = white
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(if (!isHidePassword) Res.drawable.password_open else Res.drawable.password_close),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 16.dp, top = 13.dp).size(24.dp)
                            .clickable {
                                isHidePassword = !isHidePassword
                            },
                    )
                }
            }
            Text(
                text = Strings["login"],
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 15.dp)
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
                        if (!phone.isValidPhoneNumber()) {
                            toast(true, Strings["please_check_phone"])
                            return@clickable
                        }
                        if (loginType == 0 && otp.isBlank()) {
                            toast(true, Strings["please_check_otp"])
                            return@clickable
                        }
                        if (loginType != 0 && password.isBlank()) {
                            toast(true, Strings["password_not_empty"])
                            return@clickable
                        }
                        scope.launch {
                            "startLocation".log()
                            if (CacheManager.getLocation().first == 0.0) {
                                CacheManager.saveLocation(getLastKnownLocation() ?: Pair(0.0, 0.0))
                            }
                            "endLocation:${CacheManager.getLocation().first}".log()
                            loginViewModel.login(
                                phone,
                                if (loginType == 0) otp else null,
                                if (loginType != 0) password else null
                            )
                        }
                    },
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
            )
            Spacer(modifier = Modifier.wrapContentWidth().weight(1f))
            MultiColoredText(
                Strings["accept_policy"].format(
                    Strings["privacy_agreement"],
                    Strings["privacy_blue"]
                ),
                listOf(
                    ColoredTextPart(Strings["privacy_agreement"], C_FC7700, 12.sp) {
                        onNavigate(
                            Screen.WebView(
                                Strings["privacy_agreement"],
                                CacheManager.AGREEMENT_REGISTER
                            )
                        )
                    },
                    ColoredTextPart(Strings["privacy_blue"], C_FC7700, 12.sp) {
                        onNavigate(
                            Screen.WebView(
                                Strings["privacy_blue"],
                                CacheManager.PRIVACY_POLICY
                            )
                        )
                    },
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
            )
        }
        CustomerDialog(
            show = isShowCustomerDialog && customerData != null,
            homeBean = customerData,
            onDismiss = {
                isShowCustomerDialog = false
            }
        )
        LoadingDialog(isLoading)
    }
}

@Composable
fun CustomerDialog(
    show: Boolean,
    homeBean: HomeBean?,
    onDismiss: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .background(white, RoundedCornerShape(12.dp))
                .padding(bottom = 12.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(Res.drawable.dialog_customer_bg),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
            Image(
                modifier = Modifier.padding(10.dp).size(24.dp).clickable {
                    onDismiss()
                }.align(Alignment.TopEnd),
                painter = painterResource(Res.drawable.dialog_close),
                contentDescription = null,
            )
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Image(
                    modifier = Modifier.padding(top = 15.dp).size(109.dp)
                        .align(Alignment.CenterHorizontally),
                    painter = painterResource(Res.drawable.dialog_customer),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                )
                Spacer(modifier = Modifier.height(8.dp))
                homeBean?.customerPhone?.let {
                    ContactUsItem(
                        0, Strings["phone_number"], it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 8.dp)
                            .height(64.dp)
                            .background(white),
                    )
                }
                homeBean?.customerEmail?.let {
                    ContactUsItem(
                        1, Strings["email"], it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 0.dp)
                            .height(64.dp)
                            .background(white),
                    )
                }
                homeBean?.customerConfigs?.forEach {
                    ContactUsItem(
                        if (it.buttonType == 2) 2 else 3,
                        if (CacheManager.getLanguage() == "vi") it.vernacularTitle else it.enTitle,
                        it.content,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 0.dp)
                            .height(64.dp)
                            .background(white),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreViewLogin() {
    LoginScreen {
    }
}
