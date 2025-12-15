package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.openCameraPermissionSettings
import com.kmp.vayone.postCameraPermissions
import com.kmp.vayone.ui.widget.AutoSizeText
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.viewmodel.CertViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_524F4C
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.dialog_close
import vayone.composeapp.generated.resources.example_card1
import vayone.composeapp.generated.resources.example_card2
import vayone.composeapp.generated.resources.example_card3
import vayone.composeapp.generated.resources.example_card4
import vayone.composeapp.generated.resources.example_self1
import vayone.composeapp.generated.resources.example_self2
import vayone.composeapp.generated.resources.example_self3
import vayone.composeapp.generated.resources.example_self4
import vayone.composeapp.generated.resources.kyc_camera_bg
import vayone.composeapp.generated.resources.kyc_card
import vayone.composeapp.generated.resources.kyc_self
import vayone.composeapp.generated.resources.kyc_upload_success

@Composable
fun CertKycScreen(
    isCert: Boolean = false,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val certViewModel = remember { CertViewModel() }
    var isShowConfirmExitDialog by remember { mutableStateOf(false) }
    var isShowExampleDialog by remember { mutableStateOf(false) }
    var isExampleCardDialog by remember { mutableStateOf(true) }
    val isLoading by certViewModel.isLoading.collectAsState()
    val kycConfig by certViewModel.kycConfig.collectAsState()
    val scope = rememberCoroutineScope()
    var isShowCameraPermissionDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        certViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    LaunchedEffect(Unit) {
        certViewModel.getKycConfig()
    }


    Scaffold(modifier = Modifier.fillMaxSize().background(white).statusBarsPadding(), topBar = {
        TopBar(
            "KYC",
//            rightText = if (isCert) "" else "${getAuthConfigList().indexOf("KYC") + 1}/${getAuthConfigList().filterNot { it == "BANK" }.size}"
        ) {
            if (isCert) {
                onBack()
            } else {
                isShowConfirmExitDialog = true
            }
        }
    }, bottomBar = {
        if (!isCert) {
            Text(
                text = Strings["next"],
                modifier = Modifier
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
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (kycConfig?.KYC_FRONT != 0 || kycConfig?.KYC_BACK != 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(44.dp).background(C_FFF4E6)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.kyc_card),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = Strings["nic_card"],
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            color = C_FC7700,
                            modifier = Modifier.padding(start = 6.dp).weight(1f)
                        )
                        Text(
                            text = Strings["example_str"],
                            fontSize = 13.sp,
                            lineHeight = 24.sp,
                            color = C_FC7700,
                            modifier = Modifier
                                .background(white, RoundedCornerShape(30.dp))
                                .border(
                                    width = 1.dp, color = C_FC7700, RoundedCornerShape(30.dp)
                                )
                                .clip(RoundedCornerShape(30.dp))
                                .padding(horizontal = 10.dp)
                                .clickable {
                                    isExampleCardDialog = true
                                    isShowExampleDialog = true
                                }
                        )
                    }
                    if (kycConfig?.KYC_FRONT != 0) {
                        Row(
                            modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                                Text(
                                    text = Strings["nic_card_front"],
                                    fontWeight = FontWeight.Bold,
                                    color = C_524F4C,
                                    fontSize = 16.sp,
                                )
                                Text(
                                    text = Strings["please_upload_nic_card_front"],
                                    fontWeight = FontWeight.Normal,
                                    color = C_7E7B79,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Box(
                                modifier = Modifier.size(153.dp, 106.dp)
                                    .clickable(enabled = !isCert) {
                                        scope.launch {
                                            postCameraPermissions(refuseAction = {
                                                isShowCameraPermissionDialog = true
                                            }) {

                                            }
                                        }
                                    }) {
                                Image(
                                    painter = painterResource(Res.drawable.kyc_camera_bg),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds,
                                )
                                AutoSizeText(
                                    Strings["upload_nic_card_front"],
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 2.dp, end = 2.dp, bottom = 30.dp).align(
                                            Alignment.BottomCenter
                                        ),
                                    minFontSize = 8.sp,
                                    maxFontSize = 12.sp,
                                    color = C_B4B0AD,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                )
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                        .background(color = Color.Black.copy(0.3f)),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.kyc_upload_success),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = Strings["re_up"],
                                        fontSize = 12.sp,
                                        lineHeight = 12.sp,
                                        color = white
                                    )
                                }
                            }
                        }
                    }
                    if (kycConfig?.KYC_BACK != 0) {
                        Row(
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth().clickable(enabled = !isCert) {
                                    scope.launch {
                                        postCameraPermissions(refuseAction = {
                                            isShowCameraPermissionDialog = true
                                        }) {

                                        }
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                                Text(
                                    text = Strings["nic_card_back"],
                                    fontWeight = FontWeight.Bold,
                                    color = C_524F4C,
                                    fontSize = 16.sp,
                                )
                                Text(
                                    text = Strings["please_upload_nic_card_back"],
                                    fontWeight = FontWeight.Normal,
                                    color = C_7E7B79,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Box(modifier = Modifier.size(153.dp, 106.dp)) {
                                Image(
                                    painter = painterResource(Res.drawable.kyc_camera_bg),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds,
                                )
                                AutoSizeText(
                                    Strings["upload_nic_card_back"],
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 2.dp, end = 2.dp, bottom = 30.dp).align(
                                            Alignment.BottomCenter
                                        ),
                                    minFontSize = 8.sp,
                                    maxFontSize = 12.sp,
                                    color = C_B4B0AD,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                )
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                        .background(color = Color.Black.copy(0.3f)),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.kyc_upload_success),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = Strings["re_up"],
                                        fontSize = 12.sp,
                                        lineHeight = 12.sp,
                                        color = white
                                    )
                                }
                            }
                        }
                    }
                }
                if (kycConfig?.FACE != 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 14.dp)
                            .height(44.dp).background(C_FFF4E6)
                            .padding(start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.kyc_self),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = Strings["self_photo"],
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            color = C_FC7700,
                            modifier = Modifier.padding(start = 6.dp).weight(1f)
                        )
                        Text(
                            text = Strings["example_str"],
                            fontSize = 13.sp,
                            lineHeight = 24.sp,
                            color = C_FC7700,
                            modifier = Modifier
                                .background(white, RoundedCornerShape(30.dp))
                                .border(
                                    width = 1.dp, color = C_FC7700, RoundedCornerShape(30.dp)
                                )
                                .clip(RoundedCornerShape(30.dp))
                                .padding(horizontal = 10.dp)
                                .clickable {
                                    isExampleCardDialog = false
                                    isShowExampleDialog = true
                                }
                        )
                    }
                    Text(
                        text = Strings["self_photo_tips"],
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = C_7E7B79,
                        modifier = Modifier.padding(16.dp)
                    )
                    Box(
                        modifier = Modifier.size(214.dp, 148.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable(enabled = !isCert) {
                                scope.launch {
                                    postCameraPermissions(refuseAction = {
                                        isShowCameraPermissionDialog = true
                                    }) {

                                    }
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.kyc_camera_bg),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                        AutoSizeText(
                            Strings["upload_self_photo"],
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 2.dp, end = 2.dp, bottom = 54.dp).align(
                                    Alignment.BottomCenter
                                ),
                            minFontSize = 8.sp,
                            maxFontSize = 12.sp,
                            color = C_B4B0AD,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .background(color = Color.Black.copy(0.3f)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.kyc_upload_success),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = Strings["re_up"],
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                color = white
                            )
                        }
                    }
                }
            }

//            if (isShowConfirmExitDialog && !isCert) {
//                val list = getAuthConfigList().filterNot { it1 -> it1.isBlank() || it1 == "BANK" }
//                val step = list.size - max(0, list.indexOf("KYC"))
//                ConfirmDialog(
//                    true,
//                    title = "",
//                    content = Strings["auth_exit_confirm"].format(step.toString()),
//                    cancel = Strings["give_up"],
//                    confirm = Strings["continue_str"],
//                    highLight = step.toString(),
//                    cancelAction = {
//                        onBack()
//                    },
//                    confirmAction = {
//                    }
//                ) {
//                    isShowConfirmExitDialog = false
//                }
//            }
            ShowExampleDialog(isShowExampleDialog, isExampleCardDialog) {
                isShowExampleDialog = false
            }
            LoadingDialog(isLoading)
            ShowCameraPermissionRefuse(isShowCameraPermissionDialog, scope) {
                isShowCameraPermissionDialog = false
            }
        }
    }
}

@Preview
@Composable
fun PreKyc() {
//    CertKycScreen(onBack = {}) {}
    ShowExampleDialog(isCard = false) {}
}

@Composable
fun ShowExampleDialog(
    show: Boolean = true,
    isCard: Boolean, onDismiss: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().background(white, RoundedCornerShape(12.dp))) {
            Image(
                painter = painterResource(Res.drawable.dialog_close),
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp, top = 10.dp)
                    .size(24.dp)
                    .align(Alignment.End)
                    .clickable {
                        onDismiss()
                    },
            )
            Text(
                text = Strings["shoot_req"],
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = C_2B2621,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 23.dp),
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,

                )
            Text(
                text = if (isCard) Strings["dialog_example_card_desc"] else Strings["dialog_example_self_desc"],
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = C_7E7B79,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 23.dp, vertical = 3.dp),
                lineHeight = 17.sp,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 23.dp, end = 23.dp, top = 27.dp)
            ) {
                Image(
                    painter = painterResource(if (isCard) Res.drawable.example_card1 else Res.drawable.example_self1),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp)
                        .weight(1f)
                        .aspectRatio(429 / 240f),
                    contentScale = ContentScale.FillBounds,
                )
                Image(
                    painter = painterResource(if (isCard) Res.drawable.example_card2 else Res.drawable.example_self2),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 10.dp)
                        .weight(1f)
                        .aspectRatio(429 / 240f),
                    contentScale = ContentScale.FillBounds,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 23.dp, end = 23.dp, top = 5.dp)
            ) {
                Text(
                    text = Strings["standard"],
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = C_2B2621,
                    modifier = Modifier.padding(end = 10.dp).weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isCard) Strings["crooked"] else Strings["dark_light"],
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = C_2B2621,
                    modifier = Modifier.padding(start = 10.dp).weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 23.dp, end = 23.dp, top = 27.dp)
            ) {
                Image(
                    painter = painterResource(if (isCard) Res.drawable.example_card3 else Res.drawable.example_self3),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp)
                        .weight(1f)
                        .aspectRatio(429 / 240f),
                    contentScale = ContentScale.FillBounds,
                )
                Image(
                    painter = painterResource(if (isCard) Res.drawable.example_card4 else Res.drawable.example_self4),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 10.dp)
                        .weight(1f)
                        .aspectRatio(429 / 240f),
                    contentScale = ContentScale.FillBounds,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 23.dp, end = 23.dp, top = 5.dp)
            ) {
                Text(
                    text = Strings["vague"],
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = C_2B2621,
                    modifier = Modifier.padding(end = 10.dp).weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isCard) Strings["reflection"] else Strings["high_light"],
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = C_2B2621,
                    modifier = Modifier.padding(start = 10.dp).weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = Strings["i_know"],
                color = white,
                fontSize = 18.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 34.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        C_FC7700,
                        RoundedCornerShape(30.dp)
                    ).clickable {
                        onDismiss()
                    }
            )
        }
    }
}

@Composable
fun ShowCameraPermissionRefuse(
    show: Boolean = true,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
) {
    ConfirmDialog(
        show,
        title = Strings["dialog_permission_title"].format(Strings["camera_str"]),
        content = "",
        confirmAction = {
            scope.launch {
                openCameraPermissionSettings()
            }
        }
    ) {
        onDismiss()
    }
}
