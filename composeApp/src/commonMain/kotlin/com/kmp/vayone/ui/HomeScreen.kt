package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.tabs.HomePage
import com.kmp.vayone.ui.tabs.MinePage
import com.kmp.vayone.ui.tabs.OrderPage
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.util.isLoggedIn
import com.kmp.vayone.viewmodel.MainViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_B4B0AD
import theme.C_FCFCFC
import theme.C_FFEADB
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.home_normal
import vayone.composeapp.generated.resources.home_select
import vayone.composeapp.generated.resources.login_customer
import vayone.composeapp.generated.resources.main_message
import vayone.composeapp.generated.resources.mine_normal
import vayone.composeapp.generated.resources.mine_select
import vayone.composeapp.generated.resources.order_normal
import vayone.composeapp.generated.resources.order_select

@Composable
fun HomeScreen(
    selectedIndex: Int,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onTabChange: (Int) -> Unit,
    navigate: (Screen) -> Unit,
) {

    val viewModel = remember { MainViewModel() }
    val unAuthData by viewModel.homeUnAuthResult.collectAsState()
    var isShowCustomerDialog by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        bottomBar = {
            val navItems = listOf(
                BottomNavItem(Strings["home"], Res.drawable.home_select, Res.drawable.home_normal),
                BottomNavItem(
                    Strings["order"],
                    Res.drawable.order_select,
                    Res.drawable.order_normal
                ),
                BottomNavItem(Strings["mine"], Res.drawable.mine_select, Res.drawable.mine_normal)
            )
            BottomNavigationBar(
                navigate,
                items = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = onTabChange
            )
        }
    ) {
        Box(
            modifier = Modifier.background(C_FCFCFC)
                .padding(
                    bottom = it.calculateBottomPadding()
                )
        ) {
            if (selectedIndex < 2) {
                Spacer(
                    modifier = Modifier.fillMaxWidth()
                        .height(137.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    C_FFEADB,
                                    C_FCFCFC
                                )
                            )
                        )
                        .statusBarsPadding()
                )
                Image(
                    painter = painterResource(Res.drawable.login_customer),
                    contentDescription = null,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 10.dp, end = 16.dp)
                        .size(28.dp)
                        .clickable {
                            isShowCustomerDialog = true
                            viewModel.getHomeUnAuthData()
                        }.align(Alignment.TopEnd)
                )
                Image(
                    painter = painterResource(Res.drawable.main_message),
                    contentDescription = null,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 10.dp, end = 52.dp)
                        .size(28.dp)
                        .clickable {
                            navigate(Screen.Message)
                        }.align(Alignment.TopEnd)
                )
                Text(
                    text = Strings["app_name"],
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = C_2B2621,
                    lineHeight = 28.sp,
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopCenter).padding(top = 14.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(top = if (selectedIndex < 2) 52.dp else 0.dp)
            ) {
                when (selectedIndex) {
                    0 -> HomePage(toast, navigate)
                    1 -> OrderPage(toast)
                    2 -> MinePage(toast, navigate)
                }
            }
            CustomerDialog(
                show = isShowCustomerDialog && unAuthData != null,
                homeBean = unAuthData,
                onDismiss = {
                    isShowCustomerDialog = false
                }
            )
            LoadingDialog(isLoading)
        }
    }
}

data class BottomNavItem(
    val label: String,
    val iconSelected: DrawableResource,
    val iconUnselected: DrawableResource,
)

@Composable
fun BottomNavigationBar(
    navigate: (Screen) -> Unit,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit,
) {
    Row(
        modifier = modifier.height(54.dp)
            .fillMaxWidth()
            .background(color = Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (index > 0 && !isLoggedIn()) {
                            navigate(Screen.Login)
                        } else
                            onItemSelected(index)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(
                        if (selected) item.iconSelected else item.iconUnselected
                    ),
                    contentDescription = item.label,
                    modifier = Modifier.size(24.dp),
                    alignment = Alignment.Center
                )
                Text(
                    text = item.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) C_2B2621 else C_B4B0AD,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreMainPage() {
    HomeScreen(0, onTabChange = {}) {

    }
}
