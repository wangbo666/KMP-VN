package com.kmp.vayone.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kmp.vayone.data.BannerBean
import com.kmp.vayone.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Banner(
    list: List<BannerBean>,
    autoScrollDelay: Long = 5000L,
    onItemClick: (BannerBean) -> Unit = {}
) {
    if (list.isEmpty()) return

    // 无限循环构建 [last, list..., first]
    val loopList = remember(list) {
        listOf(list.last()) + list + listOf(list.first())
    }

    val pagerState = rememberPagerState(initialPage = 1, pageCount = { loopList.size })
    val scope = rememberCoroutineScope()

    //--------------------------
    // 自动轮播
    //--------------------------
    LaunchedEffect(pagerState.currentPage) {
        while (true) {
            delay(autoScrollDelay)
            val next = pagerState.currentPage + 1

            when (next) {
                loopList.lastIndex -> {
                    // 到了假头: 无动画跳到真实第一页
                    scope.launch { pagerState.scrollToPage(1) }
                }

                else -> {
                    // 正常滚动
                    scope.launch { pagerState.animateScrollToPage(next) }
                }
            }
        }
    }

    //--------------------------
    // 无限循环边界修正（手动滑）
    //--------------------------
    LaunchedEffect(pagerState.currentPage) {
        val current = pagerState.currentPage
        val realCount = list.size

        when (current) {
            0 -> {   // 假尾，跳到真实最后一张
                scope.launch { pagerState.scrollToPage(realCount) }
            }

            loopList.lastIndex -> { // 假头，跳真实第一张
                scope.launch { pagerState.scrollToPage(1) }
            }
        }
    }

    //--------------------------
    // UI
    //--------------------------
    Box(modifier = Modifier.fillMaxWidth()) {

        //--------------------------
        // Pager
        //--------------------------
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(82.dp)
        ) { page ->

            val item = loopList[page]
            BannerItem(
                item = item,
                modifier = Modifier.clickable {
                    onItemClick(item)
                }
            )
        }

        //--------------------------
        // 指示器
        //--------------------------
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val realIndex = (pagerState.currentPage - 1).coerceIn(0, list.lastIndex)

            repeat(list.size) { i ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(width = 9.dp, height = 3.dp)
                        .background(
                            if (i == realIndex) Color(0xFFFC7700)
                            else Color(0xFFE3E0DD),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                )
            }
        }
    }
}

@Composable
fun BannerItem(item: BannerBean, modifier: Modifier = Modifier) {
    // 用你自己的图片加载：Coil、GlideCompose 均可
    "picUrl:${item.activityPicUrl}".log()
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(item.activityPicUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.FillBounds
    )
}

