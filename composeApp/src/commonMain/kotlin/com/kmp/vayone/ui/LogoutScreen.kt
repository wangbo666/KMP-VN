package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.TopBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_132247
import theme.C_6A707D
import theme.C_7E7B79
import theme.C_E3E0DD
import theme.C_ED4744
import theme.C_F9F9F9
import theme.C_FC7700
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.check_gray
import vayone.composeapp.generated.resources.check_yellow


@Composable
fun LogoutScreen(
    toast: (show: Boolean, message: String) -> Unit,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding(), topBar = {
        TopBar(Strings["close_account"]) {
            onBack()
        }
    }) {
        var selectedItems by remember { mutableStateOf(setOf<Int>()) }

        Column(
            modifier = Modifier.fillMaxSize().background(white).padding(it)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                    .wrapContentHeight()
                    .background(
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        color = C_F9F9F9
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = Strings["close_app_name"],
                    color = C_6A707D,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 18.dp, top = 16.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = Strings["app_name"],
                    color = C_132247,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(end = 18.dp, top = 16.dp),
                    textAlign = TextAlign.End
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .wrapContentHeight()
                    .background(
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                        color = C_F9F9F9
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = Strings["close_app_name"],
                    color = C_6A707D,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 18.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = Strings["app_name"],
                    color = C_132247,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                        .padding(end = 18.dp, top = 10.dp, bottom = 16.dp),
                    textAlign = TextAlign.End
                )
            }
            Text(
                text = Strings["close_account_tips"],
                color = C_ED4744,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                    .background(shape = RoundedCornerShape(12.dp), color = C_FFF4E6)
                    .padding(18.dp)
            ) {
                Text(
                    text = Strings["close_account_reason"],
                    color = C_132247,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 6.dp, end = 6.dp)
                ) {
                    listOf(
                        Strings["close_account_reason1"],
                        Strings["close_account_reason2"],
                        Strings["close_account_reason3"],
                        Strings["close_account_reason4"]
                    ).forEachIndexed { index, text ->
                        MultiSelectItem(
                            text = text,
                            isSelected = selectedItems.contains(index),
                            onClick = {
                                selectedItems =
                                    if (selectedItems.contains(index)) {
                                        selectedItems - index
                                    } else {
                                        selectedItems + index
                                    }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                Modifier.padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(C_FC7700).clickable {
                        if (selectedItems.isEmpty()) {
                            val toastMessage = Strings["please_select_one"]
                            val showToast = true
                            toast(showToast, toastMessage)
                        } else {
                            onNavigate(Screen.LogoutSuccess)
                        }
                    }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = Strings["submit"],
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = white,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MultiSelectItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val textColor = if (isSelected) C_FC7700 else C_7E7B79
    val iconRes = if (isSelected) Res.drawable.check_yellow else Res.drawable.check_gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Preview
@Composable
fun PreLogout() {
    LogoutScreen({ _, _ -> }, {}) {

    }
}