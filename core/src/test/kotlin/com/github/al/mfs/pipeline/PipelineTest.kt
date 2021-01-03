package com.github.al.mfs.pipeline

import com.github.al.mfs.DefaultCrypto
import com.github.al.mfs.io.FixedCountSplitter
import com.github.al.mfs.io.ImprovedSequenceInputStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Collections
import kotlin.random.Random

val passphrase = "1111111111".toCharArray()

class PipelineTest {

    @Test
    internal fun name() {
        val input = Random.Default.nextBytes(1000)

        val crypto1 = DefaultCrypto(passphrase)
        val splitter = FixedCountSplitter(10)
        val collector1 = ByteArrayChunkCollector(splitter)
        val pipeline1 = DefaultOutputStreamPipeline(collector1)

        pipeline1.addMapper(OutputEncryptorHeaderWriter(crypto1))
        pipeline1.addMapper(OutputCompressor())
        pipeline1.addMapper(OutputEncryptor(crypto1))

        val chunks = pipeline1.process(InputStreamInput("", ByteArrayInputStream(input))).invoke()

        val crypto2 = DefaultCrypto(passphrase)
        val collector2 = PassthroughOutputCollector(ByteArrayOutputStream())
        val pipeline2 = DefaultInputStreamPipeline(collector2)

        pipeline2.addMapper(InputDecryptorHeaderReader(crypto2))
        pipeline2.addMapper(InputDecompressor())
        pipeline2.addMapper(InputDecryptor(crypto2))

        val inputs = chunks.map { it.inputStream }
        val enumeration = Collections.enumeration(inputs)
        val sequence = ImprovedSequenceInputStream(enumeration)

        val result = pipeline2.process(InputStreamInput("", sequence))

        assertThat(result.toByteArray()).isEqualTo(input)
    }
}
