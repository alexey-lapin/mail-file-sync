package com.github.al.mfs.pipeline

import com.github.al.mfs.io.BoundedRandomCountSplitter
import com.github.al.mfs.io.FixedCountSplitter
import com.github.al.mfs.io.NoopSplitter
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Disabled
internal class SplittingInputStreamCollectorTest {
    @Test
    fun name1() {
        val collector = SplittingInputStreamCollector(NoopSplitter())
        val input = InputStreamInput("zxcv", ByteArrayInputStream(byteArrayOf(1, 2, 3, 4, 5, 6)))
        val collect = collector.collect(input, collector.getSink(input))
        collect.forEach {
            val out = ByteArrayOutputStream()
            it.inputStream.copyTo(out)
            println(out.toByteArray().contentToString())
        }
    }

    @Test
    fun name2() {
        val collector = SplittingInputStreamCollector(FixedCountSplitter(2))
        val input = InputStreamInput("zxcv", ByteArrayInputStream(byteArrayOf(1, 2, 3, 4, 5, 6)))
        val collect = collector.collect(input, collector.getSink(input))
        collect.forEach {
            val out = ByteArrayOutputStream()
            it.inputStream.copyTo(out)
            println(out.toByteArray().contentToString())
        }
    }

    @Test
    fun name3() {
        val collector = SplittingInputStreamCollector(BoundedRandomCountSplitter(1, 4))
        val input = InputStreamInput("zxcv", ByteArrayInputStream(byteArrayOf(1, 2, 3, 4, 5, 6)))
        val collect = collector.collect(input, collector.getSink(input))
        collect.forEach {
            val out = ByteArrayOutputStream()
            it.inputStream.copyTo(out)
            println(out.toByteArray().contentToString())
        }
    }
}
