package com.github.al.mfs.receiver.callback

import com.github.al.mfs.ChunkMetadata
import com.github.al.mfs.receiver.Receivable
import com.github.al.mfs.receiver.ReceiverChunkMetadataCustomizer
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface Initializer : (List<Receivable>) -> ChunkMetadata

class DefaultInitializer(
    private val receiverChunkMetadataCustomizer: ReceiverChunkMetadataCustomizer
) : Initializer {
    override fun invoke(receivables: List<Receivable>): ChunkMetadata {
        val subject = receivables[0].getSubject()
        val chunkMetadataString = subject.substringAfter(".")
        val chunkMetadata = receiverChunkMetadataCustomizer.invoke(chunkMetadataString)
        logger.info { "initialized: $chunkMetadata" }
        return chunkMetadata
    }
}











