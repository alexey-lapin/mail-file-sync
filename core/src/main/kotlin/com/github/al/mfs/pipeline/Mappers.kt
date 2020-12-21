package com.github.al.mfs.pipeline

import com.github.al.mfs.Crypto
import com.github.al.mfs.receiver.ReceiverFeatures.PAYLOAD_CONTENT_DECOMPRESS
import com.github.al.mfs.receiver.ReceiverFeatures.PAYLOAD_CONTENT_DECRYPT
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_CONTENT_COMPRESS
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_CONTENT_ENCRYPT
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
    override fun invoke(stream: OutputStream): OutputStream {
        return DeflaterOutputStream(stream, Deflater(9))
    }
}

class OutputEncryptor(private val crypto: Crypto) : OutputPipelineMapper {
    override val name = PAYLOAD_CONTENT_ENCRYPT
    override fun invoke(stream: OutputStream): OutputStream {
        return CipherOutputStream(stream, crypto.encryptionCipher())
    }
}

class InputDecompressor : InputPipelineMapper {
    override val name = PAYLOAD_CONTENT_DECOMPRESS
    override fun invoke(stream: InputStream): InputStream {
        return InflaterInputStream(stream, Inflater())
    }
}

class InputDecryptor(private val crypto: Crypto) : InputPipelineMapper {
    override val name = PAYLOAD_CONTENT_DECRYPT
    override fun invoke(stream: InputStream): InputStream {
        return CipherInputStream(stream, crypto.decryptionCipher())
    }
}
