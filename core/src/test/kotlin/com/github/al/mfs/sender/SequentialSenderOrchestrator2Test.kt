package com.github.al.mfs.sender

import com.github.al.mfs.io.NoopSplitter
import com.github.al.mfs.pipeline.ByteArrayChunkCollector
import com.github.al.mfs.pipeline.Chunk
import com.github.al.mfs.pipeline.DefaultOutputStreamPipeline
import com.github.al.mfs.pipeline.FileChunkCollector
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.File

@Disabled
internal class SequentialSenderOrchestrator2Test {

    @Test
    internal fun name() {
        val senderOrchestrator2 = SequentialSenderOrchestrator(
            FakeSender(),
            DefaultOutputStreamPipeline(ByteArrayChunkCollector(NoopSplitter())),
            MockSenderContextFactory()
        )
        senderOrchestrator2.send(File("build.gradle.kts"))
    }

    @Test
    internal fun name2() {
        val collector = FileChunkCollector(NoopSplitter(), false)
        val senderOrchestrator = SequentialSenderOrchestrator(
            FakeSender(),
            DefaultOutputStreamPipeline(collector),
            DefaultSenderContextFactory(
                "zzz",
                StringSenderChunkMetadataCustomizer(),
                ChunkSenderPayloadNameCustomizer()
            )
        )
        senderOrchestrator.send(File(""))
        collector.sourceParts
    }

}

class MockSenderContextFactory : SenderContextFactory {
    override fun create(chunk: Chunk): SenderContext {
        return SimpleSenderContext(chunk.metadata,"", "", ByteArrayInputStream(byteArrayOf()))
    }
}