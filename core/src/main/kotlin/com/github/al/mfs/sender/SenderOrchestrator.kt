package com.github.al.mfs.sender

import com.github.al.mfs.pipeline.Chunk
import com.github.al.mfs.pipeline.FileInput
import com.github.al.mfs.pipeline.OutputPipeline
import com.github.al.mfs.pipeline.Pipeline
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import javax.inject.Provider

private val logger = KotlinLogging.logger {}

interface SenderOrchestrator {
    fun send(file: File)
}

class SequentialSenderOrchestrator(
    private val sender: Sender,
    private val pipeline: OutputPipeline<List<Chunk>>,
    private val senderContextFactory: SenderContextFactory,
    private val features: List<String>
) : SenderOrchestrator {
    override fun send(file: File) {
        logger.info { "sending ${file.absolutePath} ${Files.size(file.toPath())}" }
        logger.info { "applied features: $features" }
        pipeline.let { usedPipeline ->
            logger.info { "pipeline started" }
            val chunks = usedPipeline.process(FileInput(file))
            logger.info { "pipeline finished" }
            chunks.forEach { chunk ->
                val context = senderContextFactory.create(chunk)
                sender.send(context)
            }
        }
    }
}

class ConcurrentSenderOrchestrator(
    private val sender: Provider<Sender>,
    private val pipeline: Pipeline<List<Chunk>, *>,
    private val senderContextFactory: SenderContextFactory,
    private val features: List<String>,
    private val pool: ExecutorService
) : SenderOrchestrator {

    override fun send(file: File) {
        logger.info { "sending ${file.absolutePath} ${Files.size(file.toPath())}" }
        logger.info { "transmission id: ${senderContextFactory.transmissionId}" }
        logger.info { "applied features: $features" }
        pipeline.let { usedPipeline ->
            logger.info { "pipeline started" }
            val chunks = usedPipeline.process(FileInput(file))
            logger.info { "pipeline finished" }
            val futures = chunks
                .map { chunk -> { sender.get().send(senderContextFactory.create(chunk)) } }
                .map { r -> CompletableFuture.runAsync(r, pool) }
                .toTypedArray()
            CompletableFuture.allOf(*futures).join()
            pool.shutdown()
        }
    }
}
