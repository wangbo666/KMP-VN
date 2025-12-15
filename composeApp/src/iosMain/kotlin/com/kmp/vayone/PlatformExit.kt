package com.kmp.vayone

import platform.UIKit.UIApplication
import platform.Foundation.NSLog
import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
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

}

actual suspend fun openCameraPermissionSettings() {

}
