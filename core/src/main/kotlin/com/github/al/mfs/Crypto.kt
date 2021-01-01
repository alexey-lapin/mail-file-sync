package com.github.al.mfs

import com.github.al.mfs.CryptoProperties.DEFAULT_ITERATION_COUNT
import com.github.al.mfs.CryptoProperties.DEFAULT_KEY_LENGTH
import com.github.al.mfs.CryptoProperties.DEFAULT_NONCE_LENGTH
import com.github.al.mfs.CryptoProperties.DEFAULT_SALT_LENGTH
import com.github.al.mfs.CryptoProperties.DEFAULT_TAG_LENGTH
import com.github.al.mfs.CryptoProperties.ITERATION_COUNT
import com.github.al.mfs.CryptoProperties.KEY_LENGTH
import com.github.al.mfs.CryptoProperties.NONCE_LENGTH
import com.github.al.mfs.CryptoProperties.PASSPHRASE
import com.github.al.mfs.CryptoProperties.SALT_LENGTH
import com.github.al.mfs.CryptoProperties.TAG_LENGTH
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Singleton

object CryptoProperties {
    const val PASSPHRASE = "crypto.passphrase"
    const val SALT = "crypto.salt"
    const val SALT_LENGTH = "crypto.salt.length"
    const val NONCE = "crypto.nonce"
    const val NONCE_LENGTH = "crypto.nonce.length"
    const val KEY_LENGTH = "crypto.key.length"
    const val TAG_LENGTH = "crypto.tag.length"
    const val ITERATION_COUNT = "crypto.iteration.count"

    const val DEFAULT_SALT_LENGTH = 16
    const val DEFAULT_NONCE_LENGTH = 12
    const val DEFAULT_KEY_LENGTH = 256
    const val DEFAULT_TAG_LENGTH = 128
    const val DEFAULT_ITERATION_COUNT = 300_000
}

interface Crypto {

    fun generateSalt(): ByteArray

    fun generateNonce(): ByteArray

    fun encryptionCipher(salt: ByteArray, nonce: ByteArray): Cipher

    fun decryptionCipher(salt: ByteArray, nonce: ByteArray): Cipher

    fun encryptString(input: ByteArray): ByteArray

    fun decryptString(input: ByteArray): ByteArray
}

@Requires(property = PASSPHRASE)
@Factory
class CryptoConfig {
    @Singleton
    fun crypto(
        @Property(name = PASSPHRASE) passphrase: CharArray,
        @Property(name = SALT_LENGTH, defaultValue = DEFAULT_SALT_LENGTH.toString()) saltLength: Int,
        @Property(name = NONCE_LENGTH, defaultValue = DEFAULT_NONCE_LENGTH.toString()) nonceLength: Int,
        @Property(name = KEY_LENGTH, defaultValue = DEFAULT_KEY_LENGTH.toString()) keyLength: Int,
        @Property(name = TAG_LENGTH, defaultValue = DEFAULT_TAG_LENGTH.toString()) tagLength: Int,
        @Property(name = ITERATION_COUNT, defaultValue = DEFAULT_ITERATION_COUNT.toString()) iterationCount: Int
    ): Crypto {
        return DefaultCrypto(passphrase, saltLength, nonceLength, keyLength, tagLength, iterationCount)
    }
}

class DefaultCrypto(
    private val passphrase: CharArray,
    private val saltLength: Int = DEFAULT_SALT_LENGTH,
    private val nonceLength: Int = DEFAULT_NONCE_LENGTH,
    private val keyLength: Int = DEFAULT_KEY_LENGTH,
    private val tagLength: Int = DEFAULT_TAG_LENGTH,
    private val iterationCount: Int = DEFAULT_ITERATION_COUNT
) : Crypto {

    private val random = SecureRandom.getInstanceStrong()

    override fun generateSalt(): ByteArray {
        val salt = ByteArray(saltLength)
        random.nextBytes(salt)
        return salt
    }

    override fun generateNonce(): ByteArray {
        val salt = ByteArray(nonceLength)
        random.nextBytes(salt)
        return salt
    }

    override fun encryptionCipher(salt: ByteArray, nonce: ByteArray): Cipher {
        return cipher(Cipher.ENCRYPT_MODE, salt, nonce)
    }

    override fun decryptionCipher(salt: ByteArray, nonce: ByteArray): Cipher {
        return cipher(Cipher.DECRYPT_MODE, salt, nonce)
    }

    private fun cipher(mode: Int, salt: ByteArray, nonce: ByteArray): Cipher {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val spec: KeySpec = PBEKeySpec(passphrase, salt, iterationCount, keyLength)
        val tmp = factory.generateSecret(spec)
        val skey = SecretKeySpec(tmp.encoded, "AES")

        val ivspec = GCMParameterSpec(tagLength, nonce)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(mode, skey, ivspec)

        return cipher
    }

    override fun encryptString(input: ByteArray): ByteArray {
        val salt = generateSalt()
        val nonce = generateNonce()
        val cipher = cipher(Cipher.ENCRYPT_MODE, salt, nonce)
        val encryptedBytes = cipher.doFinal(input)
        val buffer = ByteBuffer.allocate(saltLength + nonceLength + encryptedBytes.size)
        buffer.put(salt)
        buffer.put(nonce)
        buffer.put(encryptedBytes)
        return buffer.array()
    }

    override fun decryptString(input: ByteArray): ByteArray {
        val buffer = ByteBuffer.wrap(input)
        val salt = buffer.getFilled(ByteArray(saltLength))
        val nonce = buffer.getFilled(ByteArray(nonceLength))
        val encrypted = buffer.getFilled(ByteArray(buffer.remaining()))
        val cipher = cipher(Cipher.DECRYPT_MODE, salt, nonce)
        return cipher.doFinal(encrypted)
    }
}

private fun ByteBuffer.getFilled(buffer: ByteArray): ByteArray {
    this[buffer]
    return buffer
}
