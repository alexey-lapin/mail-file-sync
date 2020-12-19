package com.github.al.mfs.receiver

import com.github.al.mfs.ChunkMetadata
import com.github.al.mfs.Crypto
import com.github.al.mfs.PartMarker
import com.github.al.mfs.fromBase64

interface ReceiverChunkMetadataCustomizer : (String) -> ChunkMetadata

class NamedReceiverChunkMetadataCustomizer(private val name: String) : ReceiverChunkMetadataCustomizer {
    override fun invoke(metadata: String): ChunkMetadata {
        return ChunkMetadata(name, PartMarker.whole())
    }
}

class DecryptReceiverChunkMetadataCustomizer(private val crypto: Crypto) : ReceiverChunkMetadataCustomizer {
    override fun invoke(metadata: String): ChunkMetadata {
        return ChunkMetadata.from(crypto.decryptString(metadata.fromBase64()).decodeToString())
    }
}