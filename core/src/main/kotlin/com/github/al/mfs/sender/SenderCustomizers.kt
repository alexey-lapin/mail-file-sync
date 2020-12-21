package com.github.al.mfs.sender

import com.github.al.mfs.ChunkMetadata
import com.github.al.mfs.Crypto
import com.github.al.mfs.SenderFeature
import com.github.al.mfs.sender.SenderFeatures.METADATA_ENCRYPT
import com.github.al.mfs.sender.SenderFeatures.METADATA_PLAIN
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_NAME_OBFUSCATE
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_NAME_ORIGINAL
import com.github.al.mfs.toBase64
import java.util.UUID

interface SenderChunkMetadataCustomizer : SenderFeature, (ChunkMetadata) -> String

class PlainStringSenderChunkMetadataCustomizer : SenderChunkMetadataCustomizer {
    override val name = METADATA_PLAIN
    override fun invoke(metadata: ChunkMetadata) = metadata.toString()
}

class EncryptSenderChunkMetadataCustomizer(private val crypto: Crypto) : SenderChunkMetadataCustomizer {
    override val name = METADATA_ENCRYPT
    override fun invoke(metadata: ChunkMetadata) = crypto.encryptString(metadata.toString().toByteArray()).toBase64()
}

interface SenderPayloadNameCustomizer : SenderFeature, (ChunkMetadata) -> String

class OriginalSenderPayloadNameCustomizer : SenderPayloadNameCustomizer {
    override val name = PAYLOAD_NAME_ORIGINAL
    override fun invoke(metadata: ChunkMetadata) = if (metadata.marker.total == 1) {
        metadata.sourceFileName
    } else {
        "${metadata.sourceFileName}-${metadata.marker.current}.part"
    }
}

class ObfuscateSenderPayloadNameCustomizer : SenderPayloadNameCustomizer {
    override val name = PAYLOAD_NAME_OBFUSCATE
    override fun invoke(metadata: ChunkMetadata) = UUID.randomUUID().toString()
}
