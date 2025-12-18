@file:OptIn(ExperimentalForeignApi::class)

package com.kmp.vayone

import androidx.compose.ui.geometry.Rect
import com.kmp.vayone.ui.widget.Stroke
import kotlinx.cinterop.*
import platform.CoreGraphics.*
import platform.UIKit.*
import platform.Foundation.*

// iosMain/kotlin/com/vay/bay/ui/signature/SignatureFileManager.ios.kt

import platform.UIKit.*
import platform.CoreGraphics.*
import platform.Foundation.*

class IosSignatureFileManager : SignatureFileManager {
    override suspend fun saveSignatureImage(
        strokes: List<Stroke>,
        cropRect: Rect,
        originalSize: SignatureFileManager.Size
    ): String? {
        val width = originalSize.width.toDouble()
        val height = originalSize.height.toDouble()
        val size = CGSizeMake(width, height)

        // 1. 绘制完整图片
        val renderer = UIGraphicsImageRenderer(size)
        val fullImage = renderer.imageWithActions { context ->
            UIColor.whiteColor.setFill()
            context?.fillRect(CGRectMake(0.0, 0.0, width, height))

            UIColor.blackColor.setStroke()
            val uiPath = UIBezierPath()
            uiPath.lineWidth = 8.0
            uiPath.lineCapStyle = CGLineCap.kCGLineCapRound
            uiPath.lineJoinStyle = CGLineJoin.kCGLineJoinRound

            strokes.forEach { stroke ->
                if (stroke.points.isNotEmpty()) {
                    uiPath.moveToPoint(CGPointMake(stroke.points[0].x.toDouble(), stroke.points[0].y.toDouble()))
                    for (i in 1 until stroke.points.size) {
                        uiPath.addLineToPoint(CGPointMake(stroke.points[i].x.toDouble(), stroke.points[i].y.toDouble()))
                    }
                }
            }
            uiPath.stroke()
        }

        // 2. 裁剪 (Cropping)
        val cropRectCg = CGRectMake(
            cropRect.left.toDouble(),
            cropRect.top.toDouble(),
            cropRect.width.toDouble(),
            cropRect.height.toDouble()
        )

        // 注意：iOS Retina屏幕Scale处理，这里简化为标准CGImage裁剪
        val cgImage = fullImage.CGImage?.let {
            CGImageCreateWithImageInRect(it, cropRectCg)
        } ?: return null

        val croppedImage = UIImage.imageWithCGImage(cgImage)

        // 3. 保存 (iOS存到临时目录)
        val imageData = UIImagePNGRepresentation(croppedImage) ?: return null
        val fileName = "sign_${NSDate().timeIntervalSince1970}.png"
        val fileManager = NSFileManager.defaultManager
        val tempDir = NSTemporaryDirectory()
        val filePath = tempDir + fileName

        return if (fileManager.createFileAtPath(filePath, imageData, null)) {
            filePath
        } else {
            null
        }
    }
}

actual fun getSignatureFileManager(): SignatureFileManager = IosSignatureFileManager()