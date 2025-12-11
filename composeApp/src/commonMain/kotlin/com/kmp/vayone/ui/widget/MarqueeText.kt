package com.kmp.vayone.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.util.format
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MarqueeText(
    texts: List<String> = generateMaskedNumbers(),
    interval: Long = 5000L // 每 5 秒切换一次
) {
    if (texts.isEmpty()) return

    // 每页 3 条
    val pages = remember(texts) { texts.chunked(3) }

    var pageIndex by remember { mutableStateOf(0) }

    // 自动轮播（类似 ViewFlipper）
    LaunchedEffect(Unit) {
        while (true) {
            delay(interval)
            pageIndex = (pageIndex + 1) % pages.size
        }
    }

    // 切换动画：向上进入 / 向上退出
    AnimatedContent(
        targetState = pageIndex,
        transitionSpec = {
            slideInVertically { it } + fadeIn() togetherWith
                    slideOutVertically { -it } + fadeOut()
        },
        label = "marquee"
    ) { index ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val group = pages[index]

            val lines = listOf(
                group.getOrNull(0) ?: "",
                group.getOrNull(1) ?: "",
                group.getOrNull(2) ?: ""
            )

            lines.forEachIndexed { i, text ->
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = when (i) {
                        1 -> Color(0xFF524F4C) // 中间
                        else -> Color(0xFFB4B0AD) // 上下
                    },
                    lineHeight = 15.sp,
                    fontSize = if (i == 1) 11.sp else 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun generateMaskedNumbers(count: Int = 30): List<String> {
    val prefixes = listOf(
        "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039",
        "089", "090", "093", "070", "079", "077", "076", "078",
        "088", "091", "094", "081", "082", "083", "084", "085",
        "092", "056", "058",
        "099", "059"
    )

    return List(count) {
        val prefix = prefixes.random()
        val suffix = Random.nextInt(10, 99)
        Strings["users_notice"].format(
            "$prefix*****$suffix"
        )
    }
}

