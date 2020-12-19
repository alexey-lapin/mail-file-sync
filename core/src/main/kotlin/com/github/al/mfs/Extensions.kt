package com.github.al.mfs

import java.util.*

fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun ByteArray.toBase64() : String {
    return Base64.getEncoder().encodeToString(this)
}

fun String.toBase64() : ByteArray {
    return Base64.getEncoder().encode(this.toByteArray())
}

fun String.fromBase64() : ByteArray {
    return Base64.getDecoder().decode(this)
}

