package com.kmp.vayone

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import com.kmp.vayone.ui.widget.Stroke

expect fun exitApp()

expect fun currentTimeMillis(): Long

expect fun convertToMD5(t: String): String

expect fun mobileType(): String

expect fun getPhoneModel(): String
expect fun getPhoneBrand(): String

expect suspend fun getLastKnownLocation(): Pair<Double, Double>?

expect fun calculateAmount(list: List<String?>?): String

/**
 * 跨平台设备唯一标识符
 * 特点：卸载应用后保持不变
 * 不需要任何权限
 */
expect suspend fun getDeviceId(): String

/**
 * 打开系统的应用权限设置页
 */
expect fun openSystemPermissionSettings()

/**
 * KMP 通用权限请求，Android 请求：粗略定位、通知、读取电话状态、读取短信、读取通话记录；
 * iOS 请求：通知、粗略定位（近似定位由系统控制为“精确/模糊”）
 */
expect suspend fun postAllPermissions(
    refuseAction: (isNever: Boolean, permissions: List<String>) -> Unit = { _, _ -> },
    action: (permissions: List<String>) -> Unit,
)

expect suspend fun postCameraPermissions(
    refuseAction: (isNever: Boolean) -> Unit,
    agreeAction: () -> Unit,
)

expect suspend fun openCameraPermissionSettings()

expect suspend fun compressImage(
    imageBytes: ByteArray,
    maxSizeKb: Int = 250
): ByteArray?

expect fun randomUUID(): String


// 3. 定义跨平台保存接口 (Expect)
interface SignatureFileManager {
    // 返回保存后的文件绝对路径
    suspend fun saveSignatureImage(strokes: List<Stroke>, cropRect: Rect, originalSize: Size): String?

    data class Size(val width: Int, val height: Int)
}

expect fun getSignatureFileManager(): SignatureFileManager
