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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.vayone.data.AuthBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.viewmodel.CertViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_B4B0AD
import theme.C_FC7700
import theme.C_FFBB48
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.cert_icon
import vayone.composeapp.generated.resources.mine_right

@Composable
fun CertScreen(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val certViewModel = remember { CertViewModel() }
    val loadingState by certViewModel.loadingState.collectAsState()
    val certList by certViewModel.authList.collectAsState()

    LaunchedEffect(Unit) {
        certViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                certViewModel.getAuthStatus(false)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(white)) {
        Column(
            modifier = Modifier.fillMaxWidth().background(
                brush = Brush.horizontalGradient(
                    listOf(
                        C_FC7700, C_FFBB48
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
        ) {
            TopBar(
                title = Strings["cert_center"],
                tintColor = white,
                modifier = Modifier.statusBarsPadding().fillMaxWidth()
                    .height(44.dp)
            ) {
                onBack()
            }
            Text(
                text = Strings["certification"],
                fontWeight = FontWeight.Bold,
                color = white,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 14.dp)
            )
            Text(
                text = Strings["cert_desc"],
                fontWeight = FontWeight.Normal,
                color = white,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
            )
        }
        LoadingBox(
            loadingState, onRetry = {}, modifier = Modifier.weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(top = 11.dp)
            ) {
                items(certList.size, key = { it }) {
                    CertItem(certList[it], it) { item ->
                        when (if (item.isCertified) item.title else certList.first { it1 -> !it1.isCertified }.title) {
                            Strings["kyc_certification"] -> {
                                navigate(Screen.KycCert(item.isCertified))
                            }

                            Strings["personal_info"] -> {
                                navigate(Screen.PersonalCert(item.isCertified))
                            }

                            Strings["service_provider"] -> {
                                navigate(Screen.ServiceCert(item.isCertified))
                            }

                            Strings["supple_info"] -> {
//                                SuppleInfoActivity.launch(context, item.isCertified)
                            }

                            else -> {
                                navigate(Screen.BankCert(item.isCertified))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CertItem(item: AuthBean, index: Int, onClick: (AuthBean) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(92.dp)
            .padding(horizontal = 16.dp)
            .clickable {
                onClick(item)
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 18.dp, start = 36.dp)
                .height(56.dp)
                .background(C_FFF4E6, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 56.sp,
                textAlign = TextAlign.Start,
                color = if (item.isCertified) C_524F4C else C_B4B0AD,
                modifier = Modifier.padding(start = 46.dp)
                    .weight(1f)
            )
            Image(
                painter = painterResource(Res.drawable.mine_right),
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp).size(24.dp),
            )
        }
        Box(
            modifier = Modifier.padding(top = 10.dp).size(72.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.cert_icon),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = (index + 1).toString(),
                color = white,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 24.sp
            )
        }

    }
}

@Preview
@Composable
fun PreCert() {
    CertItem(AuthBean(), 0) {}
}