package com.github.al.mfs.pipeline

import com.github.al.mfs.BytesChunk
import com.github.al.mfs.ChunkMetadata
import com.github.al.mfs.FileChunk
import com.github.al.mfs.InputStreamChunk
import com.github.al.mfs.PartMarker
import com.github.al.mfs.io.BoundedInputStream
import com.github.al.mfs.io.LazyDelegateOutputStream
import com.github.al.mfs.io.NoopOutputStream
import com.github.al.mfs.io.Splitter
import com.github.al.mfs.io.SplittingOutputStream
import mu.KotlinLogging
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files

private val logger = KotlinLogging.logger {}

class NoopCollector : Collector<List<Chunk>> {
    override fun getSink(input: Input): OutputStream {
        return NoopOutputStream
    }

    override fun collect(input: Input, output: OutputStream): List<Chunk> {
        return listOf(InputStreamChunk(input.inputStream, ChunkMetadata(input.name, PartMarker.whole())))
    }

    override fun close() {}
}

class FileChunkCollector(
    private val splitter: Splitter,
    private val namer: (String, Int) -> String = { name, index -> "mfs-s-part-$name-${index + 1}-" },
    private val cleanupOnClose: Boolean = true
) : Collector<List<Chunk>> {

    internal val sourceParts = mutableListOf<Pair<File, FileOutputStream>>()

    override fun getSink(input: Input): OutputStream {
        return SplittingOutputStream(splitter) { i ->
            val tmpPartFile = Files.createTempFile(namer.invoke(input.name, i), null).toFile()
            val tmpPartFileOutputStream = FileOutputStream(tmpPartFile)
            sourceParts.add(tmpPartFile to tmpPartFileOutputStream)
            tmpPartFileOutputStream
        }
    }

    override fun collect(input: Input, output: OutputStream): List<Chunk> {
        logger.debug { "collecting" }
        input.inputStream.copyTo(output)
        return sourceParts.mapIndexed { i, (sourcePart, _) ->
            val metadata = ChunkMetadata(input.name, PartMarker(i + 1, sourceParts.size))
            FileChunk(sourcePart, metadata)
        }
    }

    override fun close() {
        sourceParts.forEach { (sourcePart, sourcePartOutputStream) ->
            sourcePartOutputStream.close()
            if (cleanupOnClose) {
                Files.delete(sourcePart.toPath())
            }
        }
        if (cleanupOnClose) {
            sourceParts.clear()
        }
    }
}

class ByteArrayChunkCollector(private val splitter: Splitter) : Collector<() -> List<Chunk>> {

    private val sourceParts = mutableListOf<ByteArrayOutputStream>()

    override fun getSink(input: Input): OutputStream {
        return SplittingOutputStream(splitter) {
            val bytes = ByteArrayOutputStream()
            sourceParts.add(bytes)
            bytes
        }
    }

    override fun collect(input: Input, output: OutputStream): () -> List<Chunk> {
        input.inputStream.copyTo(output)
        return {
            sourceParts.mapIndexed { i, sourcePart ->
                val metadata = ChunkMetadata(input.name, PartMarker(i + 1, sourceParts.size))
                BytesChunk(sourcePart.toByteArray(), metadata)
            }
        }
    }

    override fun close() {
    }
}

class PassthroughInputCollector : Collector<InputStream> {
    override fun getSink(input: Input): OutputStream {
        return NoopOutputStream
    }

    override fun collect(input: Input, output: OutputStream): InputStream {
        return input.inputStream
    }

    override fun close() {}
}

class PassthroughOutputCollector<T : OutputStream>(private val sink: T) : Collector<T> {
    override fun getSink(input: Input): OutputStream {
        return sink
    }

    override fun collect(input: Input, output: OutputStream): T {
        input.inputStream.copyTo(sink)
        return sink
    }

    override fun close() {}
}

class DelegateFileCollector : Collector<File> {

    private val delegate: LazyDelegateOutputStream = LazyDelegateOutputStream()

    override fun getSink(input: Input): OutputStream {
        return delegate
    }

    override fun collect(input: Input, output: OutputStream): File {
        val file = File(input.name)
        delegate.delegate = FileOutputStream(file)
        input.inputStream.copyTo(output)
        return file
    }

    override fun close() {}
}

class FileCollector : Collector<File> {

    lateinit var file: File

    override fun getSink(input: Input): OutputStream {
        file = File(input.name)
        return FileOutputStream(input.name)
    }

    override fun collect(input: Input, output: OutputStream): File {
        input.inputStream.copyTo(output)
        return file
    }

    override fun close() {}
}

class SplittingInputStreamCollector(private val splitter: Splitter) : Collector<Sequence<Chunk>> {
    override fun getSink(input: Input): OutputStream {
        return NoopOutputStream
    }

    override fun collect(input: Input, output: OutputStream): Sequence<Chunk> {
        var index = 0
        return generateSequence {
            if (input.inputStream.available() > 0) {
                splitter.reset()
                return@generateSequence InputStreamChunk(
                    BoundedInputStream(input.inputStream, splitter.getLimit()),
                    ChunkMetadata(input.name, PartMarker(++index, -1))
                )
            }
            null
        }
    }

    override fun close() {}
}
