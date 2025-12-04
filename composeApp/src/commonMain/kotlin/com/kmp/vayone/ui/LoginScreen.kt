package com.kmp.vayone.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.data.CacheManager

@Composable
fun LoginScreen(navigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("登录页")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            CacheManager.setLoggedIn(true)
            navigate(Screen.Home)
        }) {
            Text("登录")
        }
    }
}
