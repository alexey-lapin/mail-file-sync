package com.github.al.mfs.receiver

import com.github.al.mfs.ChunkMetadata
import com.github.al.mfs.Crypto
import com.github.al.mfs.PartMarker
import com.github.al.mfs.ReceiverFeature
import com.github.al.mfs.fromBase64
import com.github.al.mfs.receiver.ReceiverFeatures.METADATA_DECRYPT
import com.github.al.mfs.receiver.ReceiverFeatures.METADATA_NAMED
import com.github.al.mfs.receiver.ReceiverFeatures.METADATA_PLAIN

interface ReceiverChunkMetadataCustomizer : ReceiverFeature, (String) -> ChunkMetadata

class NamedReceiverChunkMetadataCustomizer(private val fileName: String) : ReceiverChunkMetadataCustomizer {
    override val name = METADATA_NAMED
    override fun invoke(metadata: String): ChunkMetadata {
        return ChunkMetadata(fileName, PartMarker.whole())
    }
}

class PlainStringChunkMetadataCustomizer : ReceiverChunkMetadataCustomizer {
    override val name = METADATA_PLAIN
    override fun invoke(metadata: String): ChunkMetadata {
        return ChunkMetadata.from(metadata)
    }
}

class DecryptReceiverChunkMetadataCustomizer(private val crypto: Crypto) : ReceiverChunkMetadataCustomizer {
    override val name = METADATA_DECRYPT
    override fun invoke(metadata: String): ChunkMetadata {
        return ChunkMetadata.from(crypto.decryptString(metadata.fromBase64()).decodeToString())
    }
}
