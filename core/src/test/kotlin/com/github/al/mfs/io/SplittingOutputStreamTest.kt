package com.github.al.mfs.io

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

@Disabled
internal class SplittingOutputStreamTest {

    @Test
    internal fun name() {
        val outputs = mutableListOf<OutputStream>()
        val output = SplittingOutputStream(NoopSplitter()) {
            val b = ByteArrayOutputStream()
            outputs.add(b)
            b
        }
        val bytes = byteArrayOf(1, 2, 3, 4, 5)

        ByteArrayInputStream(bytes).copyTo(output)
        println(outputs)
        val outputStream = outputs[0]
        if (outputStream is ByteArrayOutputStream) {
            val toByteArray = outputStream.toByteArray()
            println(toByteArray)
        }
    }

    @Test
    internal fun name2() {
        val outputs = mutableListOf<OutputStream>()
        val output = SplittingOutputStream(FixedCountSplitter(5)) {
            val downstreamOutput = ByteArrayOutputStream()
            outputs.add(downstreamOutput)
            downstreamOutput
        }

        val bytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        ByteArrayInputStream(bytes).copyTo(output)


        for (downstreamOutput in outputs) {
            if (downstreamOutput is ByteArrayOutputStream) {
                val toByteArray = downstreamOutput.toByteArray()
                println(toByteArray.contentToString())
            }
        }

    }

    @Test
    internal fun name3() {
        val outputs = mutableListOf<OutputStream>()
        val output = SplittingOutputStream(BoundedRandomCountSplitter(5, 10)) {
            val downstreamOutput = ByteArrayOutputStream()
            outputs.add(downstreamOutput)
            downstreamOutput
        }

        val bytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        ByteArrayInputStream(bytes).copyTo(output)


        for (downstreamOutput in outputs) {
            if (downstreamOutput is ByteArrayOutputStream) {
                val toByteArray = downstreamOutput.toByteArray()
                println(toByteArray.contentToString())
            }
        }

    }

}

