package com.github.al.mfs.sender

import com.github.al.mfs.ChunkMetadata
import com.github.al.mfs.Crypto
import com.github.al.mfs.toBase64
import java.util.UUID

interface SenderChunkMetadataCustomizer : (ChunkMetadata) -> String

class StringSenderChunkMetadataCustomizer : SenderChunkMetadataCustomizer {
    override fun invoke(metadata: ChunkMetadata) = metadata.toString()
}

class EncryptSenderChunkMetadataCustomizer(private val crypto: Crypto) : SenderChunkMetadataCustomizer {
    override fun invoke(metadata: ChunkMetadata) = crypto.encryptString(metadata.toString().toByteArray()).toBase64()
}

interface SenderPayloadNameCustomizer : (ChunkMetadata) -> String

class ChunkSenderPayloadNameCustomizer : SenderPayloadNameCustomizer {
    override fun invoke(metadata: ChunkMetadata) = if (metadata.marker.total == 1) {
        metadata.sourceFileName
    } else {
        "${metadata.sourceFileName}-${metadata.marker.current}.part"
    }
}

class ObfuscateSenderPayloadNameCustomizer : SenderPayloadNameCustomizer {
    override fun invoke(metadata: ChunkMetadata) = UUID.randomUUID().toString()
}
