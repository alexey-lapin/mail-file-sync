package com.github.al.mfs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultCryptoTest {
    @Test
    fun should_encryptAndDecryptString() {
        val plaintext = "qwer"

        val c1 = DefaultCrypto(charArrayOf('a', 'b', 'c'))
        val encrypted = c1.encryptString(plaintext.toByteArray())

        val c2 = DefaultCrypto(charArrayOf('a', 'b', 'c'))
        val decrypted = c2.decryptString(encrypted)

        val restored = String(decrypted)

        assertThat(plaintext).isEqualTo(restored)
    }
}
