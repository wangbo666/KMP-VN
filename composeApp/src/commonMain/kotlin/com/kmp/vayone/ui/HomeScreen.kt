package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.tabs.HomePage
import com.kmp.vayone.ui.tabs.MinePage
import com.kmp.vayone.ui.tabs.OrderPage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import theme.C_2B2621
import theme.C_B4B0AD
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.home_normal
import vayone.composeapp.generated.resources.home_select
import vayone.composeapp.generated.resources.mine_normal
import vayone.composeapp.generated.resources.mine_select
import vayone.composeapp.generated.resources.order_normal
import vayone.composeapp.generated.resources.order_select

@Composable
fun HomeScreen(navigate: (Screen) -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }

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
                items = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) {
        Box(modifier = Modifier.background(Color.Blue)) {
            when (selectedIndex) {
                0 -> HomePage()
                1 -> OrderPage()
                2 -> MinePage()
            }
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
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onItemSelected: (index: Int) -> Unit,
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
                    .clickable { onItemSelected(index) },
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
