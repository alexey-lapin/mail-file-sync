package com.github.al.mfs.pipeline

import com.github.al.mfs.Crypto
import com.github.al.mfs.CryptoProperties.NONCE
import com.github.al.mfs.CryptoProperties.SALT
import com.github.al.mfs.io.DelegateInputStream
import com.github.al.mfs.io.DelegateOutputStream
import com.github.al.mfs.receiver.ReceiverFeatures.PAYLOAD_CONTENT_DECOMPRESS
import com.github.al.mfs.receiver.ReceiverFeatures.PAYLOAD_CONTENT_DECRYPT
import com.github.al.mfs.receiver.ReceiverFeatures.PAYLOAD_CONTENT_DECRYPT_HEADER
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_CONTENT_COMPRESS
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_CONTENT_ENCRYPT
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_CONTENT_ENCRYPT_HEADER
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream

class OutputCompressor : OutputPipelineMapper {
    override val name = PAYLOAD_CONTENT_COMPRESS
    override fun invoke(stream: OutputStream, pipeline: Pipeline<*, OutputStream>): OutputStream {
        return DeflaterOutputStream(stream, Deflater(9))
    }
}

class OutputEncryptorHeaderWriter(private val crypto: Crypto) : OutputPipelineMapper {
    override val name = PAYLOAD_CONTENT_ENCRYPT_HEADER
    override fun invoke(stream: OutputStream, pipeline: Pipeline<*, OutputStream>): OutputStream {
        val salt = crypto.generateSalt()
        val nonce = crypto.generateNonce()
        pipeline.context[SALT] = salt
        pipeline.context[NONCE] = nonce
        stream.write(salt)
        stream.write(nonce)
        return DelegateOutputStream(stream) {}
    }
}

class OutputEncryptor(private val crypto: Crypto) : OutputPipelineMapper {
    override val name = PAYLOAD_CONTENT_ENCRYPT
    override fun invoke(stream: OutputStream, pipeline: Pipeline<*, OutputStream>): OutputStream {
        val salt = pipeline.context[SALT] as ByteArray
        val nonce = pipeline.context[NONCE] as ByteArray
        return CipherOutputStream(stream, crypto.encryptionCipher(salt, nonce))
    }
}

class InputDecompressor : InputPipelineMapper {
    override val name = PAYLOAD_CONTENT_DECOMPRESS
    override fun invoke(stream: InputStream, pipeline: Pipeline<*, InputStream>): InputStream {
        return InflaterInputStream(stream, Inflater())
    }
}

class InputDecryptor(private val crypto: Crypto) : InputPipelineMapper {
    override val name = PAYLOAD_CONTENT_DECRYPT
    override fun invoke(stream: InputStream, pipeline: Pipeline<*, InputStream>): InputStream {
        val salt = pipeline.context[SALT] as ByteArray
        val nonce = pipeline.context[NONCE] as ByteArray
        return CipherInputStream(stream, crypto.decryptionCipher(salt, nonce))
    }
}

class InputDecryptorHeaderReader(private val crypto: Crypto) : InputPipelineMapper {
    override val name = PAYLOAD_CONTENT_DECRYPT_HEADER
    override fun invoke(stream: InputStream, pipeline: Pipeline<*, InputStream>): InputStream {
        val salt = crypto.generateSalt()
        val nonce = crypto.generateNonce()
        stream.read(salt)
        stream.read(nonce)
        pipeline.context[SALT] = salt
        pipeline.context[NONCE] = nonce
        return DelegateInputStream(stream) {}
    }
}
