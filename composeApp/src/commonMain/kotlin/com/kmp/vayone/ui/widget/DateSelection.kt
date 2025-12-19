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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.EnumBean
import com.kmp.vayone.data.Strings
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_FC7700
import theme.C_FEB201
import theme.white
import kotlin.math.abs
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// 修改后的 WheelPicker - 支持初始选中
@Composable
fun WheelPickerForDate(
    items: List<EnumBean>,
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,
    itemHeight: Dp = 44.dp,
    initialIndex: Int = 0,
    onSelected: (EnumBean) -> Unit
) {
    val centerIndex = visibleCount / 2

    // 直接将目标项作为首个可见项，避免默认偏移导致初始选中错位
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
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
                abs(item.offset + item.size / 2 - viewportCenter)
            }?.index ?: initialIndex
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

//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(itemHeight)
//                .border(1.dp, C_B4B0AD)
//        )
    }

    // 初始化时立即回调
    LaunchedEffect(Unit) {
        items.getOrNull(initialIndex)?.let {
            onSelected(it)
        }
    }

    // 滚动结束后回调
    LaunchedEffect(listState.isScrollInProgress, selectedIndex) {
        if (!listState.isScrollInProgress) {
            items.getOrNull(selectedIndex)?.let {
                onSelected(it)
            }
        }
    }
}

data class DateSelection(
    val year: Int,
    val month: Int,
    val day: Int
) {
    fun toLocalDate(): LocalDate = LocalDate(year, month, day)

    override fun toString(): String {
        return "${day.toString().padStart(2, '0')}-" +
                "${month.toString().padStart(2, '0')}-" +
                year.toString().padStart(4, '0')
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DatePickerBottomSheet(
    initialDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(DatePeriod(years = 18)),
    minDate: LocalDate = LocalDate(1900, 1, 1),
    maxDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    onDismiss: () -> Unit,
    onConfirm: (DateSelection) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 生成年份列表
    val years = remember(minDate, maxDate) {
        (minDate.year..maxDate.year).map {
            EnumBean(it, "$it")
        }
    }

    // 生成月份列表
    val months = remember {
        (1..12).map {
            EnumBean(it, "$it")
        }
    }

    // 当前选中的年月日
    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.monthNumber) }
    var selectedDay by remember { mutableStateOf(initialDate.dayOfMonth) }

    // 根据选中的年月动态生成日期列表
    val days = remember(selectedYear, selectedMonth) {
        val daysInMonth = getDaysInMonth(selectedYear, selectedMonth)
        (1..daysInMonth).map {
            EnumBean(it, "$it")
        }
    }

    // 如果当前选中的日期超过了该月的最大天数，调整为最大天数
    LaunchedEffect(selectedYear, selectedMonth) {
        val maxDays = getDaysInMonth(selectedYear, selectedMonth)
        if (selectedDay > maxDays) {
            selectedDay = maxDays
        }
    }

    // 计算初始索引
    val initialYearIndex = remember {
        years.indexOfFirst { it.state == initialDate.year }.coerceAtLeast(0)
    }

    val initialMonthIndex = remember {
        initialDate.monthNumber - 1
    }

    val initialDayIndex = remember {
        initialDate.dayOfMonth - 1
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white,
        scrimColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // 三列日期选择器
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 年份选择
                WheelPickerForDate(
                    items = years,
                    modifier = Modifier.weight(1f),
                    initialIndex = initialYearIndex,
                    onSelected = {
                        selectedYear = it.state
                    }
                )

                Spacer(Modifier.width(8.dp))

                // 月份选择
                WheelPickerForDate(
                    items = months,
                    modifier = Modifier.weight(1f),
                    initialIndex = initialMonthIndex,
                    onSelected = {
                        selectedMonth = it.state
                    }
                )

                Spacer(Modifier.width(8.dp))

                // 日期选择
                WheelPickerForDate(
                    items = days,
                    modifier = Modifier.weight(1f),
                    initialIndex = initialDayIndex,
                    onSelected = {
                        selectedDay = it.state
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            // 确认按钮
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
                        val selectedDate = LocalDate(selectedYear, selectedMonth, selectedDay)
                        if (selectedDate in minDate..maxDate) {
                            onConfirm(DateSelection(selectedYear, selectedMonth, selectedDay))
                            onDismiss()
                        }
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

// 获取指定年月的天数
private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }
}

// 判断是否为闰年
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

@Preview
@Composable
fun PreviewDatePicker() {
    DatePickerBottomSheet(
        initialDate = LocalDate(2007, 12, 16),
        onDismiss = {},
        onConfirm = { date ->
            println("Selected date: ${date.toString()}")
        }
    )
}