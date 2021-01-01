package com.github.al.mfs

import org.junit.jupiter.api.Test

internal class DefaultCryptoTest {

    @Test
    fun test1() {
        val c1 = DefaultCrypto(charArrayOf('a', 'b', 'c'))
        val encryptString = c1.encryptString("qwer".toByteArray())

        val c2 = DefaultCrypto(charArrayOf('a', 'b', 'c'))
        val decryptString = c2.decryptString(encryptString)

        println(decryptString.decodeToString())
    }
}
