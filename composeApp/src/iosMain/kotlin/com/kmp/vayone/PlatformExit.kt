@file:OptIn(ExperimentalForeignApi::class)

package com.kmp.vayone

import platform.UIKit.UIApplication
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreGraphics.CGFloat
import platform.Foundation.*
import platform.UIKit.*
import platform.UIKit.UIView
import platform.UIKit.UILabel
import platform.UIKit.UIFont
import platform.UIKit.UIColor
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIViewAnimationOptionCurveEaseOut
import platform.CoreGraphics.CGRectMake
import platform.darwin.DISPATCH_TIME_FOREVER
import platform.darwin.NSObject
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_signal
import platform.darwin.dispatch_semaphore_wait
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume
import kotlinx.cinterop.*
import platform.CoreGraphics.*
import platform.UIKit.*
import kotlin.math.sqrt

// 实际实现
@OptIn(ExperimentalForeignApi::class)
actual fun exitApp() {
    // 获取主窗口并结束编辑状态
    UIApplication.sharedApplication.keyWindow?.endEditing(true)
    val window = UIApplication.sharedApplication.keyWindow
    var currentVC = window?.rootViewController

    // 递归关闭所有模态视图
    while (currentVC?.presentedViewController != null) {
        currentVC = currentVC.presentedViewController
    }

    // 关闭模态视图
    currentVC?.dismissViewControllerAnimated(false, null)
}

actual fun currentTimeMillis() = (NSDate().timeIntervalSince1970 * 1000).toLong()


actual fun convertToMD5(t: String) = ""

actual fun mobileType(): String {
    return "1"
}

actual fun getPhoneModel(): String = UIDevice.currentDevice.model
actual fun getPhoneBrand(): String = "Apple"

actual suspend fun getDeviceId(): String {
    return withContext(Dispatchers.IO) {
        UIDevice.currentDevice.identifierForVendor?.UUIDString ?: ""
    }.replace("-", "")
}

actual fun calculateAmount(list: List<String?>?): String {
    val total = list?.fold(0.0) { acc, it ->
        acc + (it?.toDoubleOrNull() ?: 0.0)
    } ?: 0.0

    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
        minimumFractionDigits = 0u
        maximumFractionDigits = 2u
        groupingSeparator = ","
    }

    return formatter.stringFromNumber(NSNumber(total)) ?: "0"
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun openSystemPermissionSettings() {
    val url = NSURL(string = UIApplicationOpenSettingsURLString)
    if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun postAllPermissions(
    refuseAction: (isNever: Boolean, permissions: List<String>) -> Unit,
    action: (permissions: List<String>) -> Unit,
) {
    val granted = mutableListOf<String>()

    var notificationGranted = false
    val semaphore = dispatch_semaphore_create(0)
    val center = platform.UserNotifications.UNUserNotificationCenter.currentNotificationCenter()
    center.requestAuthorizationWithOptions(
        options = platform.UserNotifications.UNAuthorizationOptionAlert or
                platform.UserNotifications.UNAuthorizationOptionSound or
                platform.UserNotifications.UNAuthorizationOptionBadge,
        completionHandler = { grantedFlag, error ->
            notificationGranted = grantedFlag
            dispatch_semaphore_signal(semaphore)
        }
    )
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER)

    if (notificationGranted) {
        granted += "Notification"
    } else {
        refuseAction(false, listOf("Notification"))
        return
    }

    val locationManager = platform.CoreLocation.CLLocationManager()
    val status = platform.CoreLocation.CLLocationManager.authorizationStatus()
    if (status == platform.CoreLocation.kCLAuthorizationStatusNotDetermined) {
        locationManager.requestWhenInUseAuthorization()
    }
    val finalStatus = platform.CoreLocation.CLLocationManager.authorizationStatus()
    if (finalStatus == platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse ||
        finalStatus == platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
    ) {
        granted += "Location"
    } else if (finalStatus == platform.CoreLocation.kCLAuthorizationStatusDenied) {
        refuseAction(true, listOf("Location"))
        return
    } else {
        refuseAction(false, listOf("Location"))
        return
    }

    action(granted)
}

actual suspend fun postCameraPermissions(
    refuseAction: (isNever: Boolean) -> Unit,
    agreeAction: () -> Unit
) {
    // 确保在主线程检查和请求权限
    withContext(Dispatchers.Main) {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)

        when (status) {
            AVAuthorizationStatusAuthorized -> {
                agreeAction()
            }

            AVAuthorizationStatusNotDetermined -> {
                val granted = suspendCancellableCoroutine<Boolean> { cont ->
                    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                        // 直接恢复协程，不需要再 dispatch
                        if (cont.isActive) {
                            cont.resume(granted) {}
                        }
                    }
                }

                if (granted) {
                    agreeAction()
                } else {
                    refuseAction(false)
                }
            }

            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                refuseAction(true)
            }

            else -> {
                refuseAction(false)
            }
        }
    }
}

actual suspend fun openCameraPermissionSettings() {
    withContext(Dispatchers.Main) {
        val url = NSURL(string = UIApplicationOpenSettingsURLString)
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}

actual suspend fun compressImage(
    imageBytes: ByteArray,
    maxSizeKb: Int
): ByteArray? {
    try {
        // ByteArray 转 NSData
        val nsData = imageBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }

        // NSData 转 UIImage
        var image = UIImage.imageWithData(nsData) ?: return null

        var quality = 0.9
        var compressedData: NSData?

        do {
            compressedData = UIImageJPEGRepresentation(image, quality)

            if (compressedData == null) return null

            val dataSize = compressedData.length.toLong()
            val targetSize = (maxSizeKb * 1024).toLong()

            if (dataSize <= targetSize) {
                break
            }

            if (quality > 0.1) {
                quality -= 0.1
            } else {
                // 缩小尺寸
                val scaleFactor = sqrt(targetSize.toDouble() / dataSize.toDouble())
                val newSize = CGSizeMake(
                    image.size.useContents { width * scaleFactor },
                    image.size.useContents { height * scaleFactor }
                )

                UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
                image.drawInRect(
                    CGRectMake(
                    0.0,
                    0.0,
                    newSize.useContents { width },
                    newSize.useContents { height }
                ))
                image = UIGraphicsGetImageFromCurrentImageContext() ?: return null
                UIGraphicsEndImageContext()

                quality = 0.9
            }

        } while (dataSize > targetSize)

        // NSData 转 ByteArray
        return compressedData.bytes?.readBytes(compressedData.length.toInt())

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

actual fun randomUUID(): String {
    return NSUUID().UUIDString()
}
