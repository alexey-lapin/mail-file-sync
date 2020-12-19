package com.github.al.mfs.sender

import com.github.al.mfs.pipeline.Chunk

interface SenderContextFactory {
    fun create(chunk: Chunk): SenderContext
}

class DefaultSenderContextFactory(
    private val transmissionId: String,
    private val transmissionMetadataCustomizer: SenderChunkMetadataCustomizer,
    private val senderPayloadNameCustomizer: SenderPayloadNameCustomizer
) : SenderContextFactory {

    override fun create(chunk: Chunk): SenderContext {
        val metadata = transmissionMetadataCustomizer.invoke(chunk.metadata)
        val attachmentName = senderPayloadNameCustomizer.invoke(chunk.metadata)
        return SimpleSenderContext(
            chunk.metadata,
            "$transmissionId.$metadata",
            attachmentName,
            chunk.inputStream
        )
    }
}
