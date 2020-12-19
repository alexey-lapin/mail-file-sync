package com.github.al.mfs

import com.github.al.mfs.pipeline.Chunk
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

class FileChunkOld(val file: File, val metadata: ChunkMetadata)

class FileChunk(val file: File, override val metadata: ChunkMetadata) : Chunk {
    override val inputStream: InputStream
        get() = file.inputStream()
}

class BytesChunk(private val bytes: ByteArray, override val metadata: ChunkMetadata) : Chunk {
    override val inputStream: InputStream
        get() = ByteArrayInputStream(bytes)
}

class InputStreamChunk(private val stream: InputStream, override val metadata: ChunkMetadata) : Chunk {
    override val inputStream: InputStream
        get() = stream
}