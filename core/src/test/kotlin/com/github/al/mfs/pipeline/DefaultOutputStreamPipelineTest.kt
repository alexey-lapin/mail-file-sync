package com.github.al.mfs.pipeline

import com.github.al.mfs.DefaultCrypto
import com.github.al.mfs.io.BoundedInputStream
import com.github.al.mfs.io.FixedCountSplitter
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.time.Duration
import kotlin.system.measureTimeMillis

@Disabled
internal class DefaultOutputStreamPipelineTest {

    @Test
    internal fun name() {
        val pipeline = DefaultOutputStreamPipeline(ByteArrayChunkCollector(FixedCountSplitter(2)))
        val outputs = pipeline.process(object : Input {
            override val name: String = "qwer"
            override val inputStream: InputStream = ByteArrayInputStream("zzzz".toByteArray())
        })
        println(outputs)
    }

    @Test
    internal fun name2() {
        val splitter = FixedCountSplitter(20 * 1024 * 1024) // 20MB
        val collector = FileChunkCollector(splitter)
        val pipeline = DefaultOutputStreamPipeline(collector)
        pipeline.addMapper(OutputCompressor())
        pipeline.addMapper(OutputEncryptor(DefaultCrypto("qwerasdfzxcv".toCharArray())))
        val input =
            FileInput(File(""))
        var chunks: List<Chunk>? = null
        val duration = Duration.ofMillis(measureTimeMillis { chunks = pipeline.process(input) })
        println(duration)
        println(chunks?.size)
        pipeline.close()
    }

    @Test
    internal fun name3() {
        val splitter = FixedCountSplitter(5) // 20MB
        val collector = FileChunkCollector(splitter)
        val pipeline = DefaultOutputStreamPipeline(collector)
        pipeline.addMapper(OutputCompressor())
//        pipeline.mapper(Encryptor(DefaultCrypto("qwerasdfzxcv".toCharArray())))
        val input = InputStreamInput("qwer", ByteArrayInputStream(byteArrayOf(1, 2, 3, 4, 5, 6)))
        var chunks: List<Chunk>? = null
        val duration = Duration.ofMillis(measureTimeMillis { chunks = pipeline.process(input) })
        println(duration)
        println(chunks?.size)
        val out = ByteArrayOutputStream()
        chunks?.get(0)?.inputStream?.copyTo(out)
        out.toByteArray()
        pipeline.close()
    }

    @Test
    internal fun name4() {
        val input = ByteArrayInputStream(byteArrayOf(1, 2, 3, 4, 5, 6))
        val s1 = BoundedInputStream(input, 5)
        val s2 = BoundedInputStream(input, 5)
        val o1 = ByteArrayOutputStream()
        s1.copyTo(o1)
        val o2 = ByteArrayOutputStream()
        s2.copyTo(o2)
        println()
    }
}
