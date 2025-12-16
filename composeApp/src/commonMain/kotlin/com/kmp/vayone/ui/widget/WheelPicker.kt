package com.kmp.vayone.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.EnumBean
import com.kmp.vayone.data.Strings
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_FC7700
import theme.C_FEB201
import theme.white
import kotlin.math.max

@Composable
fun WheelPicker(
    items: List<EnumBean>,
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,          // 显示行数（必须是奇数）
    itemHeight: Dp = 44.dp,
    initialIndex: Int = 0,
    onSelected: (EnumBean) -> Unit
) {
    val centerIndex = visibleCount / 2

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = max(0, initialIndex - centerIndex)
    )

    val snapLayoutInfoProvider = remember(listState) {
        SnapLayoutInfoProvider(listState)
    }
    val flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider)

    val selectedIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                kotlin.math.abs(
                    item.offset + item.size / 2 - viewportCenter
                )
            }?.index ?: 0
        }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleCount),
        contentAlignment = Alignment.Center
    ) {

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * centerIndex),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.info,
                        fontSize = if (isSelected) 16.sp else 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) C_524F4C else C_B4B0AD,
                    )
                }
            }
        }

        // 中间选中框
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .border(1.dp, C_B4B0AD)
        )
    }

    // 滚动结束后回调选中项
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            items.getOrNull(selectedIndex)?.let {
                onSelected(it)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelBottomSheet(
    items: List<EnumBean>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (EnumBean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedItem by remember { mutableStateOf(items[max(initialIndex, 0)]) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {

            WheelPicker(
                items = items,
                initialIndex = max(initialIndex, 0),
                onSelected = {
                    selectedItem = it
                }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = Strings["confirm"],
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(C_FC7700, C_FEB201)
                        )
                    )
                    .clickable {
                        onConfirm(selectedItem)
                    },
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp
            )
        }
    }
}

@Preview
@Composable
fun PreWheel() {
    WheelBottomSheet(
        listOf(EnumBean(5, "Male"), EnumBean(5, "Famale")),
        onDismiss = {}) {
    }
}
