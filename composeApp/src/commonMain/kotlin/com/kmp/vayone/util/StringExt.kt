@file:OptIn(ExperimentalTime::class)

package com.kmp.vayone.util

import com.kmp.vayone.currentTimeMillis
import com.kmp.vayone.data.Strings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun String.log() {
    println(this)
}

fun String.toMD5(): String {
    return MD5.hash(this)
}

fun String.isValidPhoneNumber(): Boolean {
//    val pattern = Regex("^(0\\d{9}|[1-9]\\d{8})$")
    val pattern = Regex("^0\\d{9}$")
    return pattern.matches(this)
}

fun String.format(vararg args: String): String {
    var result = this
    args.forEach { arg ->
        result = result.replaceFirst("%s", arg)
    }
    return result
}

fun String?.toAmountString(symbol: String?): String {
    return "${this ?: ""}${symbol ?: ""}".replace(".00", "")
}

fun String.convertYMDToDMY(): String {
    val parts = this.split("-")
    return if (parts.size == 3) {
        val day = parts[2].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[0]
        "$year-$month-$day"
    } else {
        this
    }
}

fun String.convertDMYToYMD(): String {
    val parts = this.split("-")
    return if (parts.size == 3) {
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2]
        "$year-$month-$day"
    } else {
        this
    }
}

fun String.isOver18(): Boolean {
    val parts = this.split("-")
    if (parts.size < 3) return true

    val day = parts[0].toIntOrNull() ?: return true
    val month = parts[1].toIntOrNull() ?: return true
    val year = parts[2].toIntOrNull() ?: return true

    val birthDate = LocalDate(year, month, day)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    var age = today.year - birthDate.year

    // 还没过生日，要减 1
    if (
        today.monthNumber < birthDate.monthNumber ||
        (today.monthNumber == birthDate.monthNumber &&
                today.dayOfMonth < birthDate.dayOfMonth)
    ) {
        age--
    }

    return age >= 18
}

fun String.isValidIDCard(): Boolean {
//    val pattern = Regex("^(\\d{9}|\\d{12})$")
    val pattern = Regex("^\\d{12}$")
    return pattern.matches(this)
}

fun String?.permissionToString(): String {
    return when (this) {
        "android.permission.READ_PHONE_STATE" -> Strings["dialog_permission_phone"]
        "android.permission.READ_CALL_LOG" -> Strings["dialog_permission_call"]
        "android.permission.READ_CALENDAR" -> Strings["dialog_permission_calendar"]
        "android.permission.ACCESS_COARSE_LOCATION", "Location" -> Strings["dialog_permission_location"]
        "android.permission.READ_SMS" -> Strings["dialog_permission_sms"]
        "android.permission.POST_NOTIFICATIONS", "Notification" -> Strings["dialog_permission_notification"]
        "android.permission.CAMERA" -> Strings["camera_str"]
        else -> ""
    }
}