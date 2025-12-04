package com.kmp.vayone.ui.widget

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import theme.C_7E7B79

// 每段可变色文字的配置
data class ColoredTextPart(
    val text: String,                 // 文字内容
    val color: Color = Color.Black,   // 颜色
    val fontSize: TextUnit = 16.sp,   // 字号
    val fontWeight: FontWeight = FontWeight.Normal, // 粗细
    val onClick: (() -> Unit)? = null // 点击事件
)

/**
 * 富文本组件：支持多段变色可点击文字
 *
 * @param fullText 全文字符串
 * @param coloredParts 需要变色的部分列表
 *      text -> 要匹配的文字（会在全文中查找第一次出现的位置）
 *      color/fontSize/fontWeight/onClick 可选
 */
@Composable
fun MultiColoredText(
    fullText: String,
    coloredParts: List<ColoredTextPart>,
    modifier: Modifier = Modifier,
    defaultColor: Color = C_7E7B79,
    defaultFontSize: TextUnit = 12.sp,
    defaultFontWeight: FontWeight = FontWeight.Normal
) {
    val annotatedString = buildAnnotatedString {
        // 遍历全文每个字符，先全部加默认样式
        append(fullText)
        addStyle(
            style = SpanStyle(
                color = defaultColor,
                fontSize = defaultFontSize,
                fontWeight = defaultFontWeight
            ),
            start = 0,
            end = fullText.length
        )

        // 添加有特殊样式的部分
        coloredParts.forEachIndexed { index, part ->
            val startIndex = fullText.indexOf(part.text)
            if (startIndex >= 0) {
                val endIndex = startIndex + part.text.length

                pushStringAnnotation(tag = "PART$index", annotation = part.text)

                addStyle(
                    style = SpanStyle(
                        color = part.color ?: defaultColor,
                        fontSize = part.fontSize ?: defaultFontSize,
                        fontWeight = part.fontWeight ?: defaultFontWeight,
                        textDecoration = TextDecoration.None
                    ),
                    start = startIndex,
                    end = endIndex
                )
                pop()
            }
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        onClick = { offset ->
            coloredParts.forEachIndexed { index, part ->
                val startIndex = fullText.indexOf(part.text)
                val endIndex = startIndex + part.text.length
                if (startIndex >= 0 && offset in startIndex until endIndex) {
                    part.onClick?.invoke()
                }
            }
        }
    )
}
