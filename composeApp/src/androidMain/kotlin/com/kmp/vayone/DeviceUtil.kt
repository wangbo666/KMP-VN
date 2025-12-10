package com.kmp.vayone

import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import java.io.File
import java.util.UUID
import kotlin.io.readText
import kotlin.io.writeText
import kotlin.jvm.javaClass
import kotlin.jvm.javaPrimitiveType
import kotlin.let
import kotlin.text.isNullOrBlank


object DeviceUtil {

    private var cachedId: String? = null

    private fun getIMEI(slotId: Int): String? {
        try {
            val manager = AndroidApp.appContext
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val method = manager.javaClass.getMethod("getImei", Int::class.javaPrimitiveType)
            val imei = method.invoke(manager, slotId) as String?
            return imei
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * 获取唯一设备 ID（卸载重装不变，自动兜底）
     */
    fun getDeviceId(): String {
        cachedId?.let { return it }

        // 1. 尝试使用 ANDROID_ID（Android 8+ 通常稳定）
        val androidId = getIMEI(0) ?: Settings.Secure.getString(
            AndroidApp.appContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
            cachedId = androidId
            return androidId
        }

        // 2. 使用外部共享私有目录保存的 UUID（卸载后不丢失）
        val uuid = readOrCreateUUID()
        cachedId = uuid
        return uuid
    }

    /**
     * 尝试读取外部文件中的 UUID，如没有则生成保存
     */
    private fun readOrCreateUUID(): String {
        // 无需权限，但卸载后会丢失
        val mediaDir = File(AndroidApp.appContext.filesDir, ".device_id.txt")
        if (!mediaDir.exists()) mediaDir.mkdirs()

        val file = File(mediaDir, ".device_id.txt")
        if (file.exists()) return file.readText()

        val uuid = UUID.randomUUID().toString()
        file.writeText(uuid)
        return uuid
    }
}
