package com.kmp.vayone

import android.widget.Toast
import androidx.compose.runtime.Composable
import java.security.MessageDigest
import kotlin.system.exitProcess

actual fun exitApp() {
    exitProcess(0)
}

actual fun currentTimeMillis() = System.currentTimeMillis()

actual fun convertToMD5(t: String): String {
    val md = MessageDigest.getInstance("MD5")
    val bytes = md.digest(t.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

actual fun mobileType(): String {
    return "2"
}
