package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.data.version_Name
import com.kmp.vayone.ui.widget.TopBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_E3E0DD
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.logo

@Composable
fun AboutUsScreen(
    onBack: () -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding(), topBar = {
        TopBar(Strings["about_us"]) {
            onBack()
        }
    }) {
        Column(modifier = Modifier.fillMaxSize().background(white).padding(it)) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.padding(top = 130.dp).size(65.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = Strings["app_name"],
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = C_524F4C,
                modifier = Modifier.padding(top = 18.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = version_Name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = C_E3E0DD,
                modifier = Modifier.padding(top = 3.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun PreAboutUs() {
    AboutUsScreen {

    }
}