package com.kmp.vayone

import android.widget.Toast
import androidx.compose.runtime.Composable
import kotlin.system.exitProcess

actual fun exitApp() {
    exitProcess(0)
}