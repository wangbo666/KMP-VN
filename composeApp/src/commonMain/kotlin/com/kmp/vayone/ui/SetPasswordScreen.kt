package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.viewmodel.LoginViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_524F4C
import theme.C_E3E0DD
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FFBB48
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.login_password
import vayone.composeapp.generated.resources.password_close
import vayone.composeapp.generated.resources.password_open

@Composable
fun SetPasswordScreen(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onNavigate: (Screen) -> Unit
) {
    val loginViewModel = remember { LoginViewModel() }
    var password by remember { mutableStateOf("") }
    var isHidePassword by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading by loginViewModel.isLoading.collectAsState()

    var isShowSkipDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loginViewModel.setPasswordResult.collect {
            CacheManager.setToken(it?.token ?: "")
            onNavigate(
                Screen.SetPasswordSuccess(
                    Strings["password_set_successful"]
                )
            )
        }
    }
    LaunchedEffect(Unit) {
        loginViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(white)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(C_FC7700, C_FFBB48)
                        ),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .statusBarsPadding()
            ) {
                TopBar(
                    Strings["create_a_password"],
                    white,
                    modifier = Modifier.fillMaxWidth().height(44.dp).background(Color.Transparent)
                ) {
                    isShowSkipDialog = true
                }
                Text(
                    text = Strings["set_password"],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    lineHeight = 36.sp,
                    color = white,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Text(
                    text = Strings["set_password_tips"],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                    lineHeight = 20.sp,
                    color = white,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                )
            }
            Text(
                text = Strings["password"],
                fontSize = 14.sp,
                color = C_524F4C,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 35.dp, end = 35.dp, top = 27.dp),
                textAlign = TextAlign.Start
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 4.dp, start = 35.dp, end = 35.dp)
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
            Text(
                text = Strings["next"],
                modifier = Modifier
                    .padding(start = 35.dp, end = 35.dp, top = 17.dp)
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
                        if (password.isBlank()) {
                            toast(true, Strings["password_not_empty"])
                            return@clickable
                        }
                        loginViewModel.setPassword(password)
                    },
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
            )
        }
        LoadingDialog(isLoading)
        ConfirmDialog(
            isShowSkipDialog,
            confirm = Strings["setup"],
            cancel = Strings["skip"],
            cancelAction = {
                onNavigate(Screen.Home())
            },
            confirmAction = {
            }) {
            isShowSkipDialog = false
        }
    }
}

@Preview
@Composable
fun PreSetPassword() {
    SetPasswordScreen {

    }
}