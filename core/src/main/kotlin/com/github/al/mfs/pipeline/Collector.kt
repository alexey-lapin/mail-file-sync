package com.github.al.mfs.pipeline

import com.github.al.mfs.ChunkMetadata
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface Collector<R> : Closeable {

    fun getSink(input: Input): OutputStream

    fun collect(input: Input, output: OutputStream): R

}

interface Input {

    val name: String

    val inputStream: InputStream

}

class FileInput(private val file: File) : Input {
    override val name: String
        get() = file.name
    override val inputStream: InputStream
        get() = file.inputStream()
}

class InputStreamInput(override val name: String, override val inputStream: InputStream) : Input

interface Chunk {

    val metadata: ChunkMetadata

    val inputStream: InputStream

}