package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.data.Strings
import com.kmp.vayone.exitApp
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.TopBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_40495C
import theme.C_FC7700
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.permission_check
import vayone.composeapp.generated.resources.permission_uncheck

@Composable
fun PrivacyScreen(
    toast: (show: Boolean, message: String) -> Unit,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    var agreeCollection by remember { mutableStateOf(true) }
    var agreePrivacy by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(white)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopBar(title = Strings["permissions"]) {
                onBack()
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
            ) {
                Row(Modifier.padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(if (agreeCollection) Res.drawable.permission_check else Res.drawable.permission_uncheck),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                agreeCollection = !agreeCollection
                            },
                    )
                    MultiColoredText(
                        Strings["privacy_agree1"],
                        listOf(ColoredTextPart(Strings["privacy_agree1_blue"], C_FC7700, 12.sp) {
                            onNavigate(
                                Screen.WebView(
                                    Strings["privacy_agree1_blue"],
                                    CacheManager.PRIVACY_COLLECT
                                )
                            )
                        }),
                        modifier = Modifier.weight(1f).padding(start = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(if (agreePrivacy) Res.drawable.permission_check else Res.drawable.permission_uncheck),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                agreePrivacy = !agreePrivacy
                            },
                    )
                    MultiColoredText(
                        Strings["privacy_agree2"],
                        listOf(
                            ColoredTextPart(Strings["privacy_policy"], C_FC7700, 12.sp) {
                                onNavigate(
                                    Screen.WebView(
                                        Strings["privacy_policy"],
                                        CacheManager.PRIVACY_POLICY
                                    )
                                )
                            },
                            ColoredTextPart(Strings["privacy_agree2_blue"], C_FC7700, 12.sp) {
                                onNavigate(
                                    Screen.WebView(
                                        Strings["privacy_agree2_blue"],
                                        CacheManager.AGREEMENT_REGISTER
                                    )
                                )
                            },
                        ),
                        modifier = Modifier.weight(1f).padding(start = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f)
                            .padding(end = 6.dp)
                            .border(
                                width = 1.dp,                  // 边线宽度
                                color = C_FC7700,             // 边线颜色
                                shape = RoundedCornerShape(30.dp) // 圆角
                            )
                            .clip(RoundedCornerShape(30.dp))
                            .background(color = white)
                            .clickable {
                                exitApp()
                            },
                        text = Strings["reject"],
                        fontSize = 16.sp,
                        lineHeight = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = C_FC7700,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(1f)
                            .padding(start = 6.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(color = C_FC7700)
                            .clickable {
                                when {
                                    !agreeCollection -> {
                                        val toastMessage = Strings["privacy_toast_agree1"]
                                        val showToast = true
                                        toast(showToast, toastMessage)
                                    }

                                    !agreePrivacy -> {
                                        val toastMessage = Strings["privacy_toast_agree2"]
                                        val showToast = true
                                        toast(showToast, toastMessage)
                                    }

                                    else -> {
                                        CacheManager.setAgreedPrivacy(true)
                                        onNavigate(Screen.Home())
                                    }
                                }
                            },
                        lineHeight = 44.sp,
                        text = Strings["agree"],
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = white,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(white)
                .padding(paddingValues = paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permissions"].uppercase(),
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_desc"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_location_title"],
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_location_content"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_camera_title"],
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_camera_content"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_phone_title"],
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_phone_content"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_installed_title"],
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_installed_content"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permissions_notification_title"],
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permissions_notification_content"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_sms_title"],
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_sms_content"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_call_title"],
                        color = C_FC7700,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = Strings["permission_call_content"],
                        color = C_40495C,
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreViewPermission() {
    PrivacyScreen({ _, _ -> }, {}) {

    }
}
