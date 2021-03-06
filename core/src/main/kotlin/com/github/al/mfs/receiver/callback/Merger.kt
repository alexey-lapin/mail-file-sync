package com.github.al.mfs.receiver.callback

import com.github.al.mfs.ChunkMetadata
import com.github.al.mfs.FileChunk
import com.github.al.mfs.pipeline.InputPipeline
import com.github.al.mfs.pipeline.InputStreamInput
import com.github.al.mfs.receiver.Receivable
import com.github.al.mfs.receiver.ReceiverChunkMetadataCustomizer
import mu.KotlinLogging
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.SequenceInputStream
import java.nio.file.Files
import java.time.Duration
import java.util.Collections
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

interface Merger : (List<Receivable>) -> File

class TempFileMerger(
    private val receiverChunkMetadataCustomizer: ReceiverChunkMetadataCustomizer,
    private val pipeline: InputPipeline<File>
) : Merger {

    override fun invoke(receivables: List<Receivable>): File {
        val filesToChunks = receivables
            .map { it to getMetadata(it) }
            .groupBy({ it.second.sourceFileName }, { getFileChunk(it.first, it.second) })
        try {
            filesToChunks.forEach { (sourceFileName, chunks) ->
                val sortedChunks = chunks.sortedWith { a, b -> a.metadata.marker.current - b.metadata.marker.current }
                val partInputs = sortedChunks.map { chunk -> FileInputStream(chunk.file) }
                val sequenceInput = SequenceInputStream(Collections.enumeration(partInputs))
                logger.info { "pipeline started" }
                val file = pipeline.process(InputStreamInput(sourceFileName, sequenceInput))
                logger.info { "pipeline finished" }
                return file
            }
        } finally {
            filesToChunks.forEach { (_, chunks) ->
                chunks.forEach { it.file.delete() }
            }
        }
        throw RuntimeException()
    }

    private fun getMetadata(receivable: Receivable): ChunkMetadata {
        val subject = receivable.getSubject()
        val chunkMetadataString = subject.substringAfter(".")
        return receiverChunkMetadataCustomizer.invoke(chunkMetadataString)
    }

    private fun getFileChunk(receivable: Receivable, chunkMetadata: ChunkMetadata): FileChunk {
        val tempPartFileName = "mfs-r-part-${chunkMetadata.sourceFileName}-${chunkMetadata.marker.current}-"
        val tempPartFile = Files.createTempFile(tempPartFileName, null).toFile()
        logger.info { "${chunkMetadata.marker} receive started" }
        val duration = Duration.ofMillis(
            measureTimeMillis {
                FileOutputStream(tempPartFile).use {
                    receivable.loadAttachment(it)
                }
            }
        )
        logger.info { "${chunkMetadata.marker} receive finished: ${duration.toString().substring(2)}" }
        return FileChunk(tempPartFile, chunkMetadata)
    }
}
