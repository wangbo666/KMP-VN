package com.kmp.vayone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.ui.tabs.HomePage
import com.kmp.vayone.ui.tabs.MinePage
import com.kmp.vayone.ui.tabs.OrderPage

@Composable
fun HomeScreen(navigate: (Screen) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val tabs = listOf("Home", "Order", "Mine")

                tabs.forEachIndexed { index, title ->
                    BottomNavigationItem(
                        selected = selectedTab == index,
                        onClick = {
                            if (index > 0 && !CacheManager.isLoggedIn()) {
                                navigate(Screen.Login)
                            } else {
                                selectedTab = index
                            }
                        },
                        label = { Text(title) },
//                        icon = { Icon(Icons.Default.Home, null) }
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.background(Color.Blue)){
            when (selectedTab) {
                0 -> HomePage()
                1 -> OrderPage()
                2 -> MinePage()
            }
        }
    }
}

@Composable
fun BottomNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
//    icon: () -> Icon
) {
}

@Composable
fun BottomNavigation(content: @Composable () -> Unit) {

}
