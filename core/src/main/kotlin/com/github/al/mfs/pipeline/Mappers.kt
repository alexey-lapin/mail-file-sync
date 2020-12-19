package com.github.al.mfs.pipeline

import com.github.al.mfs.Crypto
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream

class OutputCompressor : OutputPipelineMapper {
    override fun invoke(stream: OutputStream): OutputStream {
        return DeflaterOutputStream(stream, Deflater(9))
    }
}

class OutputEncryptor(private val crypto: Crypto) : OutputPipelineMapper {
    override fun invoke(stream: OutputStream): OutputStream {
        return CipherOutputStream(stream, crypto.encryptionCipher())
    }
}

class InputDecompressor : InputPipelineMapper {
    override fun invoke(stream: InputStream): InputStream {
        return InflaterInputStream(stream, Inflater())
    }
}

class InputDecryptor(private val crypto: Crypto) : InputPipelineMapper {
    override fun invoke(stream: InputStream): InputStream {
        return CipherInputStream(stream, crypto.decryptionCipher())
    }
}
