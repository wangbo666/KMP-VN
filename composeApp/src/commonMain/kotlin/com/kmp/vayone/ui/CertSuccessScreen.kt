package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.util.format
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_5AE109
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_FC7700
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.cert_success

@Composable
fun CertSuccessScreen(
    navigate: (Screen) -> Unit,
) {
    var timeLeft by remember { mutableStateOf(10) }
    LaunchedEffect(key1 = timeLeft) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        // 结束后恢复状态
        navigate(Screen.Home())
    }

    Column(
        modifier = Modifier.fillMaxSize().background(white).statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopBar(title = Strings["succeed"]) {
            navigate(Screen.Home())
        }
        Image(
            painter = painterResource(Res.drawable.cert_success),
            contentDescription = null,
            modifier = Modifier.padding(top = 62.dp).size(125.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            Strings["submit_success"],
            color = C_5AE109,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 60.dp),
            textAlign = TextAlign.Center
        )
        Text(
            Strings["you_can_borrow"],
            color = C_7E7B79,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 13.sp,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        MultiColoredText(
            Strings["back_to_home_tips"].format(
                timeLeft.toString()
            ),
            listOf(
                ColoredTextPart(timeLeft.toString(), C_524F4C, 18.sp) {

                }
            ),
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            defaultColor = C_B4B0AD,
            defaultFontSize = 14.sp,
            defaultFontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        Text(
            textAlign = TextAlign.Center,
            text = Strings["back_to_home"],
            color = white,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 48.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                .background(C_FC7700, RoundedCornerShape((30.dp)))
                .clip(RoundedCornerShape((30.dp)))
                .clickable {
                    navigate(Screen.Home(isFromCertSuccess = true))
                }

        )
    }
}

@Preview
@Composable
fun PreCertSucceed() {
    CertSuccessScreen {}
}