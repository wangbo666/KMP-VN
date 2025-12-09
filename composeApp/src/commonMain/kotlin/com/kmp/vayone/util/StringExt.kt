package com.kmp.vayone.util

fun String.log(){
    println(this)
}

fun String.toMD5(): String {
    return MD5.hash(this)
}