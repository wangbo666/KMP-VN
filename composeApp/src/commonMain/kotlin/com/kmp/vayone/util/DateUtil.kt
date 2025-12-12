// commonMain/kotlin/com/you/package/DateUtil.kt
@file:OptIn(ExperimentalTime::class)

package com.kmp.vayone.util

import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.*
import kotlin.math.abs
import kotlin.time.ExperimentalTime

// ---------- 原常量（秒级） ----------
const val YEAR = 365 * 24 * 60 * 60
const val MONTH = 30 * 24 * 60 * 60
const val DAY = 24 * 60 * 60
const val HOUR = 60 * 60
const val MINUTE = 60

// 原格式常量（保留）
val dateFormatFAll = "dd/MM/yyyy"
val dateFormat = "dd/MM/yyyy"
val dateTimeFront = "yyyy-MM-dd"
val dateTimeFormat = "dd/MM/yyyy HH:mm"
val dateTimeFormatAll = "yyyy-MM-dd HH:mm:ss"
val dateTimeFormatAllBack = "dd-MM-yyyy HH:mm:ss"

// -------------------- 字符串 / 时间戳 基本转换 --------------------

/** 把 yyyy-MM-dd -> dd/MM/yyyy（与原 toddMMyyyy 等价） */
fun String.toddMMyyyy(): String = this.replace("-", "/")

/**
 * Long (epoch millis) -> 指定格式字符串（默认 "dd/MM/yyyy"）
 * pattern 支持: yyyy, MM, dd, HH, mm, ss
 */
fun Long.ddMMyyyy(pattern: String = dateFormat): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return ldt.formatWithPattern(pattern)
}

/**
 * String -> epoch millis (Long)，pattern 默认 "dd/MM/yyyy"
 * 返回 0L 表示解析失败
 */
fun String?.date(pattern: String = dateFormat): Long {
    if (this == null || this.isBlank()) return 0L
    return try {
        when (pattern) {
            "dd/MM/yyyy" -> {
                val parts = this.split("/")
                if (parts.size < 3) return 0L
                val y = parts[2].toInt()
                val m = parts[1].toInt()
                val d = parts[0].toInt()
                LocalDate(y, m, d).atStartOfDayIn(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
            }
            "yyyy-MM-dd" -> {
                val d = LocalDate.parse(this)
                d.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            }
            else -> {
                // 尝试 ISO parse
                try {
                    val ldt = LocalDateTime.parse(this)
                    ldt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                } catch (e: Exception) {
                    0L
                }
            }
        }
    } catch (e: Exception) {
        0L
    }
}

// -------------------- 其它工具方法（等价实现） --------------------

/** 计算从给定字符串（yyyy-MM-dd）到现在的年数差（等价 getYearsBetween） */
fun String.getYearsBetween(): Int {
    if (this.isBlank()) return 0
    return try {
        val given = LocalDate.parse(this) // 期望 yyyy-MM-dd
        val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        abs(now.year - given.year)
    } catch (e: Exception) {
        0
    }
}

/** Long -> "N hour ago" 或者 "dd/MM/yyyy HH:mm"（等价 getYearDay） */
fun Long.getYearDay(): String {
    val nowMs = kotlin.time.Clock.System.now().toEpochMilliseconds()
    val diffSeconds = (nowMs - this) / 1000
    return when {
        diffSeconds >= HOUR -> "${diffSeconds / HOUR} hour ago"
        else -> this.ddMMyyyy(dateTimeFormat)
    }
}

/** 获取当前时间的格式化字符串 */
fun getFormatTime(format: String): String {
    val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return now.formatWithPattern(format)
}

/** get0Day 的 KMP 版本：返回 LocalDate(1-01-01)（不可用 java.GregorianCalendar） */
fun get0Day(): LocalDate = LocalDate(1, 1, 1)

/** 获取当前 LocalDateTime（等价 currentTime: Calendar） */
val currentLocalDateTime: LocalDateTime
    get() = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * compare 两个 LocalDateTime：
 * 返回 0 相等，1 time1 > time2， -1 time1 < time2
 */
fun compare(time1: LocalDateTime, time2: LocalDateTime): Int {
    return when {
        time1 == time2 -> 0
        time1 > time2 -> 1
        else -> -1
    }
}

/**
 * daySwitchesBetween: 返回两个 LocalDateTime 相差天数（与原实现逻辑一致，参照明天零点的 fix）
 */
//fun daySwitchesBetween(time1: LocalDateTime, time2: LocalDateTime): Int {
//    val time1Millis = time1.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
//    val time2Millis = time2.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
//    var fix = 0
//    if (tomorrowOClockMillis(time1) - time1Millis < tomorrowOClockMillis(time2) - time2Millis) {
//        fix = 1
//    }
//    return ((time2Millis - time1Millis) / (24 * 60 * 60 * 1000)).toInt() + fix
//}

/** tomorrowOClock 的辅助（传入 LocalDateTime 版） */
//private fun tomorrowOClockMillis(time: LocalDateTime): Long {
//    var temp = time.date.atStartOfDayIn(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.UTC)
//    val timeMillis = time.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
//    val tempMillis = temp.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
//    if (tempMillis < timeMillis) {
//        temp = temp.plus(DateTimePeriod(days = 1))
//    }
//    return temp.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
//}

/** isToday 检查 LocalDateTime 是否是今天 */
fun isToday(dateTime: LocalDateTime): Boolean {
    val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return dateTime.date == today
}

/**
 * date(LocalDateTime) -> 返回类似 "Jan 1 Tue" 或 对中国返回 "Jan1日 周X"（尽量模拟原实现）
 */
//fun date(calendar: LocalDateTime): String {
//    val monthStr = calendar.month.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3)
//    val day = calendar.dayOfMonth
//    val weekday = calendar.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3)
//    return if (Locale.getDefault().country == "CN" || Locale.language == "zh") {
//        "$monthStr$day日 $weekday"
//    } else {
//        "$monthStr $day $weekday"
//    }
//}

/**
 * calculateStep(date: LocalDateTime, minutesInterval)
 * 计算当天从 00:00 到当前时间的 step 数： (hours*60 + minutes) / minutesInterval
 */
fun calculateStep(date: LocalDateTime, minutesInterval: Int): Int {
    val hours = date.hour
    val minutes = date.minute
    return (hours * 60 + minutes) / minutesInterval
}

/**
 * calculateStep(startDate, toDate, minutesInterval)
 * 如果两者同一天，按差值计算；否则按 toDate 的当天时间计算
 */
fun calculateStep(startDate: LocalDateTime, toDate: LocalDateTime, minutesInterval: Int): Int {
    val hours: Int
    val minutes: Int
    if (isAtStartDay(startDate, toDate)) {
        hours = toDate.hour - startDate.hour
        minutes = if (hours == 0) {
            toDate.minute - startDate.minute
        } else {
            toDate.minute
        }
    } else {
        hours = toDate.hour
        minutes = toDate.minute
    }
    return (hours * 60 + minutes) / minutesInterval
}

/** isAtStartDay 判断两个 LocalDateTime 是否在同一天 */
private fun isAtStartDay(startDate: LocalDateTime, selectedDate: LocalDateTime): Boolean {
    return (selectedDate.year == startDate.year && selectedDate.dayOfYear == startDate.dayOfYear)
}

/** time(LocalDateTime) -> "HH:mm" */
private var HHmmPatternCached: String? = null
fun time(dateTime: LocalDateTime): String {
    // 简单实现格式化 HH:mm
    val h = dateTime.hour.toString().padStart(2, '0')
    val m = dateTime.minute.toString().padStart(2, '0')
    return "$h:$m"
}

/**
 * calculateStepOffset(startDate, selectedDate, minutesInterval)
 * 参照原实现：若不在同一天返回 0，否则计算偏移
 */
fun calculateStepOffset(
    startDate: LocalDateTime,
    selectedDate: LocalDateTime,
    minutesInterval: Int,
): Int {
    if (!isAtStartDay(startDate, selectedDate)) return 0
    var stepOffset = 0
    val hourValue = startDate.hour
    var minuteValue = startDate.minute
    stepOffset += hourValue * (60 / minutesInterval)
    val remain = minuteValue % minutesInterval > 0
    minuteValue = ((minuteValue / minutesInterval + if (remain) 1 else 0) * minutesInterval)
    stepOffset += minuteValue / minutesInterval
    return stepOffset
}

/** getToday -> "yyyy-MM-dd" */
fun getToday(): String {
    val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return now.formatWithPattern("yyyy-MM-dd")
}

// -------------------- 内部格式化 / 解析辅助 --------------------

/** LocalDateTime -> 按简单 pattern 格式化（支持 yyyy, MM, dd, HH, mm, ss） */
private fun LocalDateTime.formatWithPattern(pattern: String): String {
    return pattern
        .replace("yyyy", this.year.toString().padStart(4, '0'))
        .replace("MM", this.monthNumber.toString().padStart(2, '0'))
        .replace("dd", this.dayOfMonth.toString().padStart(2, '0'))
        .replace("HH", this.hour.toString().padStart(2, '0'))
        .replace("mm", this.minute.toString().padStart(2, '0'))
        .replace("ss", this.second.toString().padStart(2, '0'))
}

/** LocalDate -> pattern 格式化（用于解析后格式化） */
private fun LocalDate.formatWithPattern(pattern: String): String {
    return pattern
        .replace("yyyy", this.year.toString().padStart(4, '0'))
        .replace("MM", this.monthNumber.toString().padStart(2, '0'))
        .replace("dd", this.dayOfMonth.toString().padStart(2, '0'))
}

/** LocalDateTime -> Instant (epoch ms) */
private fun LocalDateTime.toInstant(timeZone: TimeZone): Instant = this.toInstant(timeZone)

/** 便捷：Instant -> epoch millis */
private fun Instant.toEpochMillis(): Long = this.toEpochMilliseconds()
