package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_524F4C
import theme.C_FC7700
import theme.C_FEB201
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.password_success

@Composable
fun SetPasswordSuccessScreen(
    title: String = Strings["password_set_successful"],
    onNavigate: (Screen) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(white)) {
        Image(
            painter = painterResource(Res.drawable.password_success), contentDescription = null,
            modifier = Modifier.padding(top = 150.dp)
                .size(125.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = title,
            fontSize = 20.sp,
            color = C_2B2621,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 62.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = Strings["password_set_successful_tips"],
            fontSize = 12.sp,
            color = C_524F4C,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 8.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = Strings["complete"],
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp, top = 15.dp)
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
                    onNavigate(Screen.Home())
                },
            color = white,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 48.sp,
        )
    }
}

@Preview
@Composable
fun PreSetPasswordSuccessScreen() {
    SetPasswordSuccessScreen {}
}