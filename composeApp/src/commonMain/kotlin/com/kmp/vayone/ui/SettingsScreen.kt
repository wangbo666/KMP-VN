package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.Strings
import com.kmp.vayone.data.version_Name
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.ToastHost
import com.kmp.vayone.ui.widget.TopBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_E3E0DD
import theme.C_F9F9F9
import theme.C_FC7700
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.logo
import vayone.composeapp.generated.resources.mine_right

@Composable
fun SettingsScreen(
    toast: (show: Boolean, message: String) -> Unit,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
) {

    var isShowConfirmDialog by remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding(), topBar = {
        TopBar(Strings["settings"]) {
            onBack()
        }
    }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().background(white).padding(it)
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 46.dp).size(65.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = Strings["app_name"],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = C_524F4C,
                    modifier = Modifier.padding(top = 18.dp).align(Alignment.CenterHorizontally)
                )
                Text(
                    text = version_Name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = C_E3E0DD,
                    modifier = Modifier.padding(top = 3.dp).align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 36.dp)
                        .height(54.dp).clip(RoundedCornerShape(8.dp)).background(C_F9F9F9)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
//
                        ) {
                            onNavigate(Screen.ChangePassword)
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings["change_password"],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = C_524F4C,
                        modifier = Modifier.padding(horizontal = 24.dp).weight(1f)
                    )
                    Image(
                        painter = painterResource(Res.drawable.mine_right),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 24.dp).size(24.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                        .height(54.dp).clip(RoundedCornerShape(8.dp)).background(color = C_F9F9F9)
                        .clickable {
                            onNavigate(Screen.Logout)
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings["close_account"],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = C_524F4C,
                        modifier = Modifier.padding(horizontal = 24.dp).weight(1f)
                    )
                    Image(
                        painter = painterResource(Res.drawable.mine_right),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 24.dp).size(24.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                        .height(54.dp).clip(RoundedCornerShape(8.dp)).background(color = C_F9F9F9)
                        .clickable {
                            onNavigate(Screen.Feedback)
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings["feedback"],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = C_524F4C,
                        modifier = Modifier.padding(horizontal = 24.dp).weight(1f)
                    )
                    Image(
                        painter = painterResource(Res.drawable.mine_right),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 24.dp).size(24.dp)
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 36.dp)
                        .height(44.dp)
                        .border(width = 1.dp, color = C_FC7700, shape = RoundedCornerShape(30.dp))
                        .clip(RoundedCornerShape(30.dp))
                        .clickable {
                            isShowConfirmDialog = true
                        },
                ) {
                    Text(
                        text = Strings["logout"],
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = C_FC7700,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                ConfirmDialog(
                    isShowConfirmDialog,
                    "",
                    Strings["sure_logout"],
                    Strings["closed", Strings["sure"]],
                    confirmAction = {
                        CacheManager.setLoginInfo(null)
                        CacheManager.setToken("")
                        onNavigate(Screen.Home())
                    }
                ) {
                    isShowConfirmDialog = false
                }
            }
        }
    }
}

@Preview
@Composable
fun PreSet() {
    SettingsScreen(toast = { _, _ -> }, {}) {}
}