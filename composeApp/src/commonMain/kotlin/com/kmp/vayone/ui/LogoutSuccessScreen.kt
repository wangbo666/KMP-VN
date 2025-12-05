package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import theme.C_2B2621
import theme.C_FC7700
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.logout_icon

@Composable
fun LogoutSuccessScreen(
    onNavigate: (Screen) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding(), topBar = {
        TopBar(Strings["close_account"]) {
            onNavigate(Screen.Home())
        }
    }) {
        Column(
            modifier = Modifier.fillMaxSize().background(white).padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(Res.drawable.logout_icon),
                contentDescription = null,
                modifier = Modifier.padding(top = 35.dp).size(122.dp, 136.dp)
            )
            Text(
                text = Strings["data_has_deleted"],
                color = C_2B2621,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp),
                textAlign = TextAlign.Center
            )
            Box(
                Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(C_FC7700).clickable {
                        onNavigate(Screen.Home())
                    }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = Strings["complete"],
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = white,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun PreLogoutSuccess() {
    LogoutSuccessScreen {

    }
}
