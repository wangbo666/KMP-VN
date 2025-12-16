package com.kmp.vayone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.UUID
import kotlin.collections.arrayListOf
import kotlin.math.sqrt
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

actual fun getPhoneModel(): String = Build.MODEL
actual fun getPhoneBrand(): String = Build.BRAND

actual suspend fun getDeviceId(): String {
    return DeviceUtil.getDeviceId()
}

actual fun calculateAmount(list: List<String?>?): String {
    val fold = list?.fold(
        BigDecimal.ZERO
    ) { acc, it -> acc + BigDecimal(it) }
    val formatter = DecimalFormat("#,###.##")  // 最多保留两位小数，可调整
    return formatter.format(fold ?: BigDecimal.ZERO)
}

actual fun openSystemPermissionSettings() {
    val requested = arrayListOf<IPermission>(
        PermissionLists.getAccessCoarseLocationPermission(),
        PermissionLists.getReadPhoneStatePermission(),
        PermissionLists.getPostNotificationsPermission(),
        PermissionLists.getReadSmsPermission(),
        PermissionLists.getReadCallLogPermission(),
    )
    XXPermissions.startPermissionActivity(MainActivity.instance, requested)
}

@Suppress("UNCHECKED_CAST")
actual suspend fun postAllPermissions(
    refuseAction: (isNever: Boolean, permissions: List<String>) -> Unit,
    action: (permissions: List<String>) -> Unit,
) {
    val activity = MainActivity.instance
    try {
        val requested = arrayListOf<IPermission>().apply {
            add(PermissionLists.getAccessCoarseLocationPermission())
            add(PermissionLists.getReadPhoneStatePermission())
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                add(PermissionLists.getPostNotificationsPermission())
            }
            add(PermissionLists.getReadSmsPermission())
            add(PermissionLists.getReadCallLogPermission())
        }

        if (requested.isEmpty()) {
            action(emptyList())
            return
        }

//        val runnable = Runnable {
        XXPermissions.with(activity)
            .unchecked()
            .permissions(requested.toTypedArray())
            .request(object : OnPermissionCallback {
                override fun onGranted(
                    permissions: MutableList<IPermission>,
                    allGranted: Boolean
                ) {
                    if (allGranted) action(permissions.map { it.toString() })
                }

                override fun onDenied(
                    permissions: MutableList<IPermission>,
                    doNotAskAgain: Boolean,
                ) {
                    refuseAction(doNotAskAgain, permissions.map { it.toString() })
                }
            })
//        }
//        android.os.Handler(android.os.Looper.getMainLooper()).post(runnable)
    } catch (_: Exception) {
        refuseAction(false, emptyList())
    }
}

actual suspend fun postCameraPermissions(
    refuseAction: (isNever: Boolean) -> Unit,
    agreeAction: () -> Unit
) {
    val activity = MainActivity.instance
    XXPermissions.with(activity)
        .unchecked()
        .permission(PermissionLists.getCameraPermission())
        .request(object : OnPermissionCallback {
            override fun onGranted(
                permissions: MutableList<IPermission>,
                allGranted: Boolean
            ) {
                if (allGranted) agreeAction.invoke()
            }

            override fun onDenied(
                permissions: MutableList<IPermission>,
                doNotAskAgain: Boolean,
            ) {
                refuseAction(doNotAskAgain)
            }
        })
}

actual suspend fun openCameraPermissionSettings() {
    XXPermissions.startPermissionActivity(
        MainActivity.instance,
        PermissionLists.getCameraPermission()
    )
}

actual suspend fun compressImage(
    imageBytes: ByteArray,
    maxSizeKb: Int
): ByteArray? {
    try {
        var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // 修正方向
        bitmap = fixImageOrientation(bitmap, imageBytes)

        // 压缩
        var quality = 90
        var compressedData: ByteArray

        do {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            compressedData = stream.toByteArray()
            stream.close()

            if (compressedData.size <= maxSizeKb * 1024) {
                break
            }

            if (quality > 10) {
                quality -= 10
            } else {
                // 缩小尺寸
                val scaleFactor = sqrt((maxSizeKb * 1024).toDouble() / compressedData.size)
                val newWidth = (bitmap.width * scaleFactor).toInt()
                val newHeight = (bitmap.height * scaleFactor).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                quality = 90
            }
        } while (compressedData.size > maxSizeKb * 1024)

        bitmap.recycle()
        return compressedData

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

private fun fixImageOrientation(bitmap: Bitmap, imageBytes: ByteArray): Bitmap {
    try {
        val inputStream = imageBytes.inputStream()
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        return bitmap
    }
}

actual fun randomUUID(): String {
    return UUID.randomUUID().toString()
}