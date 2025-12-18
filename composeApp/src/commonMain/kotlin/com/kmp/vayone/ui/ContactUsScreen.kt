package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_132247
import theme.C_2B2621
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_FC7700
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.contact_email
import vayone.composeapp.generated.resources.contact_phone
import vayone.composeapp.generated.resources.contact_tel

@Composable
fun ContactUsScreen(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
) {
    val loginViewModel = remember { LoginViewModel() }

    val customerData by loginViewModel.customer.collectAsState()
    val loadingState by loginViewModel.loadingState.collectAsState()

    LaunchedEffect(Unit) {
        launch {
            loginViewModel.getCustomer()
        }
        launch {
            loginViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding(), topBar = {
        TopBar(Strings["contact_us"]) {
            onBack()
        }
    }) {
        LoadingBox(
            state = loadingState,
            modifier = Modifier.fillMaxSize().background(white).padding(it)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = Strings["contact_detail"],
                    color = C_FC7700,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 15.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = Strings["contact_detail_desc"],
                    color = C_7E7B79,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 7.dp, start = 20.dp, end = 20.dp, bottom = 4.dp),
                    textAlign = TextAlign.Center
                )
                customerData?.let { data ->
                    data.customerPhone?.let { it1 ->
                        ContactUsItem(0, Strings["phone_number"], it1)
                    }
                    data.customerEmail?.let { it1 ->
                        ContactUsItem(0, Strings["email"], it1)
                    }
                    data.customerConfigs?.forEach { it1 ->
                        ContactUsItem(
                            if (it1.buttonType == 2) 2 else 3,
                            if (CacheManager.getLanguage() == "vi") it1.vernacularTitle else it1.enTitle,
                            it1.content,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactUsItem(
    type: Int = 0,
    title: String? = "",
    content: String? = "",
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp, top = 8.dp)
        .height(82.dp)
        .background(shape = RoundedCornerShape(12.dp), color = C_FFF4E6)
        .padding(horizontal = 20.dp),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            contentDescription = null,
            painter = painterResource(
                when (type) {
                    0 -> Res.drawable.contact_phone
                    1 -> Res.drawable.contact_email
                    else -> Res.drawable.contact_tel
                }
            ),
            modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
        )
        Column(modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) {
            Text(
                text = title ?: "",
                color = C_B4B0AD,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 16.dp),
                textAlign = TextAlign.Start
            )
            Text(
                text = content ?: "",
                color = C_2B2621,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 16.dp),
                textAlign = TextAlign.Start
            )
        }
        Text(
            text = Strings[when (type) {
                0, 2 -> "call"
                else -> "copy"
            }],
            color = white,
            fontSize = 14.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.size(75.dp, 32.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(shape = RoundedCornerShape(28.dp), color = C_FC7700)
                .clickable {
                    if (type == 0 || type == 2) {//call

                    } else {//copy

                    }
                },
        )
    }
}

@Preview
@Composable
fun PreContactUs() {
    ContactUsScreen {

    }
}