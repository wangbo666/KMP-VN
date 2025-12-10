package com.kmp.vayone

import android.os.Build
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import java.security.MessageDigest
import kotlin.collections.arrayListOf
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
