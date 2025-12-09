package com.kmp.vayone

import platform.UIKit.UIApplication
import platform.Foundation.NSLog
import kotlinx.cinterop.*
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
import platform.darwin.NSObject

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