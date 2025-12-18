package com.kmp.vayone

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.geometry.Rect
import com.kmp.vayone.ui.widget.Stroke
import java.io.File
import java.io.FileOutputStream

class AndroidSignatureFileManager : SignatureFileManager {
    override suspend fun saveSignatureImage(
        strokes: List<Stroke>,
        cropRect: androidx.compose.ui.geometry.Rect,
        originalSize: SignatureFileManager.Size
    ): String? {
        return try {
            // 1. 创建完整 Bitmap
            val bitmap = Bitmap.createBitmap(originalSize.width, originalSize.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE) // 背景白

            val paint = Paint().apply {
                color = android.graphics.Color.BLACK
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 8f
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }

            // 2. 绘制路径
            strokes.forEach { stroke ->
                val path = Path()
                if (stroke.points.isNotEmpty()) {
                    path.moveTo(stroke.points[0].x, stroke.points[0].y)
                    for (i in 1 until stroke.points.size) {
                        path.lineTo(stroke.points[i].x, stroke.points[i].y)
                    }
                }
                canvas.drawPath(path, paint)
            }

            // 3. 裁剪 (Cropping) - 复刻原代码逻辑
            val width = cropRect.width.toInt().coerceAtLeast(1)
            val height = cropRect.height.toInt().coerceAtLeast(1)
            val cropped = Bitmap.createBitmap(
                bitmap,
                cropRect.left.toInt(),
                cropRect.top.toInt(),
                width,
                height
            )

            // 4. 缩放 (Scaling) - 复刻原代码逻辑 (缩小一半)
            val scaled = Bitmap.createScaledBitmap(cropped, width / 2, height / 2, true)

            // 5. 保存文件
            val file = File(AndroidApp.appContext.cacheDir, "sign_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                scaled.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

actual fun getSignatureFileManager(): SignatureFileManager = AndroidSignatureFileManager()