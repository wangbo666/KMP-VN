package com.kmp.vayone.ui.widget

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.kmp.vayone.data.ProductBean
import kotlinx.serialization.Serializable

@Serializable
data class SignPageParams(
    val cardId: Long? = null,
    val productList: List<ProductBean>? = null,
    val productId: String? = null,
    val bankId: Long? = null,
    val amount: String? = null,
    val productInstallmentMap: String? = null,
    val termIdMap: String? = null,
    val isShowBackHome: Boolean = false,
    var signPath: String? = null,
)

// 1. 定义笔迹数据结构 (点集)，方便跨平台传输
data class Stroke(
    val points: List<Offset>,
    val color: Long = 0xFF000000, // ARGB Black
    val width: Float = 8f
)

// 2. 状态管理器 (替代 View 的变量)
class SignatureController {
    var strokes = mutableStateListOf<Stroke>()
    var currentPoints = mutableStateListOf<Offset>()

    // 用于裁剪的边界 (对应你原代码的 dirtyLeft/Right...)
    private var minX = Float.MAX_VALUE
    private var maxX = Float.MIN_VALUE
    private var minY = Float.MAX_VALUE
    private var maxY = Float.MIN_VALUE

    fun start(offset: Offset) {
        currentPoints.clear()
        currentPoints.add(offset)
        updateBounds(offset)
    }

    fun move(offset: Offset) {
        currentPoints.add(offset)
        updateBounds(offset)
    }

    fun end() {
        if (currentPoints.isNotEmpty()) {
            strokes.add(Stroke(points = currentPoints.toList()))
            currentPoints.clear()
        }
    }

    fun clear() {
        strokes.clear()
        currentPoints.clear()
        resetBounds()
    }

    fun isEmpty() = strokes.isEmpty() && currentPoints.isEmpty()

    // 获取裁剪区域 (保留原逻辑)
    fun getCropRect(canvasWidth: Float, canvasHeight: Float): Rect {
        if (minX == Float.MAX_VALUE) return Rect(0f, 0f, canvasWidth, canvasHeight)
        // 增加一点 padding，防止贴边
        return Rect(
            left = (minX - 10f).coerceAtLeast(0f),
            top = (minY - 10f).coerceAtLeast(0f),
            right = (maxX + 10f).coerceAtMost(canvasWidth),
            bottom = (maxY + 10f).coerceAtMost(canvasHeight)
        )
    }

    private fun updateBounds(offset: Offset) {
        minX = minOf(minX, offset.x)
        maxX = maxOf(maxX, offset.x)
        minY = minOf(minY, offset.y)
        maxY = maxOf(maxY, offset.y)
    }

    private fun resetBounds() {
        minX = Float.MAX_VALUE; maxX = Float.MIN_VALUE
        minY = Float.MAX_VALUE; maxY = Float.MIN_VALUE
    }
}
