package com.kmp.vayone.ui.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.AutoSizeText
import com.kmp.vayone.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_190E30
import theme.C_2B2621
import theme.C_524F4C
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FFBB48
import theme.C_FFD64F
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.avatar
import vayone.composeapp.generated.resources.avatar_bg
import vayone.composeapp.generated.resources.mine_about
import vayone.composeapp.generated.resources.mine_account
import vayone.composeapp.generated.resources.mine_cert
import vayone.composeapp.generated.resources.mine_contact
import vayone.composeapp.generated.resources.mine_icon
import vayone.composeapp.generated.resources.mine_language
import vayone.composeapp.generated.resources.mine_order
import vayone.composeapp.generated.resources.mine_payback_bg
import vayone.composeapp.generated.resources.mine_privacy
import vayone.composeapp.generated.resources.mine_right
import vayone.composeapp.generated.resources.mine_set


@Composable
fun MinePage(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    navigate: (Screen) -> Unit,
) {
    val viewModel = remember { MainViewModel() }

    var isShowLanguageDialog by remember { mutableStateOf(false) }
    var isShowPaybackDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.homeAuthResult.collect {
            if (it?.showMultipleRepaySign == 1) {
                navigate(Screen.BatchRepayment)
            } else {
                isShowPaybackDialog = true
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .background(white)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(298.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(C_FFBB48, C_FC7700)
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
        ) {
            Image(
                modifier = Modifier.padding(top = 42.dp).size(174.dp).align(Alignment.TopEnd),
                contentDescription = null,
                alignment = Alignment.TopEnd,
                painter = painterResource(Res.drawable.mine_icon)
            )
            Box(modifier = Modifier.padding(top = 86.dp, start = 19.dp).size(66.dp)) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
                    painter = painterResource(Res.drawable.avatar_bg)
                )
                Image(
                    modifier = Modifier.size(60.dp).align(Alignment.Center),
                    contentDescription = null,
                    painter = painterResource(Res.drawable.avatar)
                )
            }
            Text(
                text = Strings["welcome_str"],
                color = white,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize().align(Alignment.TopStart)
                    .padding(start = 92.dp, top = 91.dp)
            )
            Text(
                text = CacheManager.getLoginInfo()?.phone ?: "",
                color = white,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize().align(Alignment.TopStart)
                    .padding(start = 92.dp, top = 116.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .align(Alignment.BottomCenter)
                    .background(color = white.copy(0.3f), shape = RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
                        .background(Color.Transparent)
                        .clickable {

                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.mine_account),
                        contentDescription = Strings["accounts"],
                        modifier = Modifier.size(57.dp),
                        alignment = Alignment.Center
                    )
                    Text(
                        modifier = Modifier.padding(top = 9.dp),
                        text = Strings["accounts"],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = C_2B2621,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {

                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.mine_order),
                        contentDescription = Strings["order_center"],
                        modifier = Modifier.size(57.dp),
                        alignment = Alignment.Center
                    )
                    AutoSizeText(
                        modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
                        text = Strings["order_center"],
                        maxFontSize = 14.sp,
                        minFontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = C_2B2621,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                        .background(Color.Transparent)
                        .clickable {

                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.mine_cert),
                        contentDescription = Strings["certification"],
                        modifier = Modifier.size(57.dp),
                        alignment = Alignment.Center
                    )
                    Text(
                        modifier = Modifier.padding(top = 9.dp),
                        text = Strings["certification"],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = C_2B2621,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(61.dp)
                .background(shape = RoundedCornerShape(12.dp), color = C_FFD64F),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                text = Strings["quick_repayment"],
                color = C_2B2621,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier.height(48.dp).align(Alignment.CenterVertically)
                    .padding(end = 7.dp)
            ) {
                Image(
                    modifier = Modifier.wrapContentSize(),
                    painter = painterResource(Res.drawable.mine_payback_bg),
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(bottom = 9.dp)
                        .align(Alignment.Center).clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                viewModel.getHomeAuthData(true)
                            }
                        ),
                    text = Strings["pay_back_now"],
                    color = white,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = C_FFF4E6, shape = RoundedCornerShape(16.dp))
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .clickable {
                                isShowLanguageDialog = true
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.mine_language),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 10.dp).size(24.dp)
                        )
                        Text(
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            text = Strings["language"],
                            color = C_524F4C,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Start
                        )
                        Image(
                            painter = painterResource(Res.drawable.mine_right),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                            .clip(RoundedCornerShape(0.dp)).clickable {
                                navigate(
                                    Screen.WebView(
                                        Strings["privacy_policy"],
                                        CacheManager.PRIVACY_POLICY
                                    )
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.mine_privacy),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 10.dp).size(24.dp)
                        )
                        Text(
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            text = Strings["privacy_policy"],
                            color = C_524F4C,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Start
                        )
                        Image(
                            painter = painterResource(Res.drawable.mine_right),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                            .clip(RoundedCornerShape(0.dp))
                            .clickable {
                                navigate(Screen.Settings)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.mine_set),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 10.dp).size(24.dp)
                        )
                        Text(
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            text = Strings["settings"],
                            color = C_524F4C,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Start
                        )
                        Image(
                            painter = painterResource(Res.drawable.mine_right),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                            .clip(RoundedCornerShape(0.dp))
                            .clickable {
                                navigate(Screen.ContactUs)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.mine_contact),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 10.dp).size(24.dp)
                        )
                        Text(
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            text = Strings["contact_us"],
                            color = C_524F4C,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Start
                        )
                        Image(
                            painter = painterResource(Res.drawable.mine_right),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                            .clickable {
                                navigate(Screen.AboutUs)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.mine_about),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 10.dp).size(24.dp)
                        )
                        Text(
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            text = Strings["about_us"],
                            color = C_524F4C,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Start
                        )
                        Image(
                            painter = painterResource(Res.drawable.mine_right),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        )
                    }
                }
            }
        }
        ChooseLanguageDialog(isShowLanguageDialog, navigate) {
            isShowLanguageDialog = false
        }
        PaybackDialog(isShowPaybackDialog, navigate) {
            isShowPaybackDialog = false
        }
    }
}

@Composable
fun ChooseLanguageDialog(
    show: Boolean,
    navigate: (Screen) -> Unit,
    onDismiss: () -> Unit,
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 0.dp)
                    .background(shape = RoundedCornerShape(16.dp), color = white),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.wrapContentSize()
                        .padding(start = 16.dp, end = 16.dp, top = 35.dp),
                    text = "Please choose language",
                    color = C_2B2621,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.wrapContentSize()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    text = "Vui lÃng chon ngón ngür",
                    color = C_2B2621,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.padding(top = 25.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                            .padding(start = 16.dp, end = 6.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(color = C_FFF4E6)
                            .clickable {
                                onDismiss()
                                if (CacheManager.getLanguage() != "en") {
                                    Strings.setLang("en")
                                    navigate(Screen.Home())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "English",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = C_FC7700,
                            textAlign = TextAlign.Center
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                            .padding(end = 16.dp, start = 6.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(color = C_FC7700)
                            .clickable {
                                onDismiss()
                                if (CacheManager.getLanguage() == "en") {
                                    Strings.setLang("vi")
                                    navigate(Screen.Home())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tiếng Việt",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = white,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreMine() {
    PaybackDialog(true, {}) {}
}

@Composable
fun PaybackDialog(
    show: Boolean,
    navigate: (Screen) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .background(white, RoundedCornerShape(16.dp))
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = Strings["empty_repay"],
                color = C_190E30,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 48.dp)
            )
            Text(
                text = Strings["borrow_now"],
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, top = 28.dp, bottom = 24.dp)
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
                        onDismiss()
                        navigate(Screen.Home())
                    },
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
            )
        }
    }
}