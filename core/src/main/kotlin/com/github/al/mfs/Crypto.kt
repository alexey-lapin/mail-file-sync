package com.github.al.mfs

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Singleton

interface Crypto {

    fun encryptionCipher() : Cipher

    fun decryptionCipher() : Cipher

    fun encryptString(input: ByteArray): ByteArray

    fun decryptString(input: ByteArray): ByteArray

}

@Requires(property = "crypto.passphrase")
@Singleton
class DefaultCrypto(@Property(name = "crypto.passphrase") private val passphrase: CharArray): Crypto {

    override fun encryptionCipher(): Cipher {
        return cipher(Cipher.ENCRYPT_MODE)
    }

    override fun decryptionCipher(): Cipher {
        return cipher(Cipher.DECRYPT_MODE)
    }

    private fun cipher(mode: Int): Cipher {
        val salt = Const.salt

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val spec: KeySpec = PBEKeySpec(passphrase, salt, 10000, 128)
        val tmp = factory.generateSecret(spec)
        val skey = SecretKeySpec(tmp.encoded, "AES")

        val iv = Const.iv
        val ivspec = GCMParameterSpec(128, iv)

        val ci = Cipher.getInstance("AES/GCM/NoPadding")
        ci.init(mode, skey, ivspec)

        return ci
    }

    private fun cryptString(input: ByteArray, mode: Int): ByteArray {
        val cipher = cipher(mode)
//        val out = ByteArrayOutputStream()
//        val out1 = cipher.update(input)
//        out.write(out1)
        val out2 = cipher.doFinal(input)
//        out.write(out2)
//        return out.toByteArray()
        return out2
    }

    override fun encryptString(input: ByteArray): ByteArray {
        return cryptString(input, Cipher.ENCRYPT_MODE)
    }

    override fun decryptString(input: ByteArray): ByteArray {
        return cryptString(input, Cipher.DECRYPT_MODE)
    }

}