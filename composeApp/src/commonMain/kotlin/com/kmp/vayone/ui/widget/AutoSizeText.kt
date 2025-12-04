package com.kmp.vayone.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.max

/**
 * 更精确的二分查找版本
 * 性能更好，但实现稍复杂
 */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit = 12.sp,
    maxFontSize: TextUnit = 24.sp,
    maxLines: Int = 1,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Start,
    fontStyle: FontStyle? = null,
    fontFamily: FontFamily? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current

    var fontSize by remember(text) { mutableStateOf(maxFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    var minSize by remember(text) { mutableStateOf(minFontSize) }
    var maxSize by remember(text) { mutableStateOf(maxFontSize) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = style.copy(
            fontSize = fontSize,
            color = if (color != Color.Unspecified) color else style.color,
            fontWeight = fontWeight ?: style.fontWeight,
            fontStyle = fontStyle ?: style.fontStyle,
            fontFamily = fontFamily ?: style.fontFamily,
            textAlign = textAlign ?: style.textAlign
        ),
        maxLines = maxLines,
        overflow = TextOverflow.Visible,
        onTextLayout = { textLayoutResult ->
            val didOverflow = textLayoutResult.didOverflowHeight || textLayoutResult.didOverflowWidth

            with(density) {
                // 使用二分查找
                if (didOverflow) {
                    maxSize = fontSize
                    fontSize = ((minSize.toPx() + maxSize.toPx()) / 2).toSp()
                } else {
                    minSize = fontSize
                    fontSize = ((minSize.toPx() + maxSize.toPx()) / 2).toSp()
                }

                // 当范围足够小时停止
                if (abs(maxSize.toPx() - minSize.toPx()) < 1f) {
                    fontSize = minSize
                    readyToDraw = true
                }
            }
        }
    )
}

