package com.github.al.mfs.sender

import com.github.al.mfs.ChunkMetadata
import java.io.InputStream

interface SenderContext {
    val metadata: ChunkMetadata
    val subject: String
    val attachmentContent: InputStream
    val attachmentName: String
}

data class SimpleSenderContext(
    override val metadata: ChunkMetadata,
    override val subject: String,
    override val attachmentName: String,
    override val attachmentContent: InputStream,
) : SenderContext
