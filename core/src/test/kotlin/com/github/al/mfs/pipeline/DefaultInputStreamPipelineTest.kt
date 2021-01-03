package com.github.al.mfs.pipeline

import com.github.al.mfs.DefaultCrypto
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.SequenceInputStream
import java.nio.file.Paths
import java.util.Collections
import java.util.zip.DeflaterInputStream

@Disabled
internal class DefaultInputStreamPipelineTest {

    @Test
    internal fun name() {
        val collector = PassthroughOutputCollector(ByteArrayOutputStream())
        val pipeline = DefaultInputStreamPipeline(collector)
        pipeline.addMapper { i, p -> DeflaterInputStream(i) }
        val b = pipeline.process(InputStreamInput("", ByteArrayInputStream(byteArrayOf(1, 2, 3, 4))))
        println(b.toByteArray().contentToString())
    }

    @Test
    internal fun name2() {
        val crypto = DefaultCrypto("1111111111".toCharArray())
        val collector = FileCollector()
        val pipeline = DefaultInputStreamPipeline(collector)

        pipeline.addMapper(InputBuffer())
        pipeline.addMapper(InputDecryptorHeaderReader(crypto))
        pipeline.addMapper(InputDecompressor())
        pipeline.addMapper(InputDecryptor(crypto))

        val dir = Paths.get("").toFile()
        val list = dir.listFiles { _, n ->
            n.startsWith("mfs-qqq-")
        }
        val inputs: List<InputStream> = list.map { FileInputStream(it) }
        val enumeration = Collections.enumeration(inputs)
        val sequence = SequenceInputStream(enumeration)

        pipeline.process(InputStreamInput("pp.zip", sequence))
    }
}
