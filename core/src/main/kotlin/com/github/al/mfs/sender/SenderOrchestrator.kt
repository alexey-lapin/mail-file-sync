package com.github.al.mfs.sender

import com.github.al.mfs.pipeline.Chunk
import com.github.al.mfs.pipeline.FileInput
import com.github.al.mfs.pipeline.OutputPipeline
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files

private val logger = KotlinLogging.logger {}

interface SenderOrchestrator {

    fun send(file: File)

}

class SequentialSenderOrchestrator(
    private val sender: Sender,
    private val pipeline: OutputPipeline<List<Chunk>>,
    private val senderContextFactory: SenderContextFactory,
) : SenderOrchestrator {
    override fun send(file: File) {
        logger.info { "sending ${file.absolutePath} ${Files.size(file.toPath())}" }
        pipeline.let { usedPipeline ->
            val chunks = usedPipeline.process(FileInput(file))
            logger.info { "pipeline finished" }
            chunks.forEach { chunk ->
                val context = senderContextFactory.create(chunk)
                sender.send(context)
            }
        }
    }
}
