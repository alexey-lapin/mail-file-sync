package com.github.al.mfs.pipeline

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterInputStream

@Disabled
internal class DefaultInputStreamPipelineTest {

    @Test
    internal fun name() {
        val pipeline = DefaultInputStreamPipeline(PassthroughOutputCollector(ByteArrayOutputStream()))
        pipeline.addMapper { i -> DeflaterInputStream(i) }
        val b = pipeline.process(InputStreamInput("", ByteArrayInputStream(byteArrayOf(1, 2, 3, 4))))
        println(b.toByteArray().contentToString())
    }

}