package com.kmp.vayone.util

fun String.log(){
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