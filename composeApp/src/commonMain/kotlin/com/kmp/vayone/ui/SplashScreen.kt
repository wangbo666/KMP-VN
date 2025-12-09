package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.Strings
import com.kmp.vayone.viewmodel.SplashViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.logo
import vayone.composeapp.generated.resources.splash
import vayone.composeapp.generated.resources.splash_icon

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = SplashViewModel(),
    onNavigate: (Screen) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getSecret {
            if (CacheManager.isAgreedPrivacy()) {
                onNavigate(Screen.Home())
            } else {
                onNavigate(Screen.Privacy)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.splash),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
        Image(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 170.dp)
                .size(67.dp),
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
        )
        Image(
            modifier = Modifier.size(432.dp).padding(end = 152.dp)
                .align(Alignment.BottomStart),
            painter = painterResource(Res.drawable.splash_icon),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 245.dp),
            text = Strings["app_name"],
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = white)
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen {

    }
}
