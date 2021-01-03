package com.github.al.mfs.io

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.Collections

internal class ImprovedSequenceInputStreamTest {
    @Test
    internal fun name() {
        val i1 = ByteArrayInputStream(byteArrayOf(1, 2, 3))
        val i2 = ByteArrayInputStream(byteArrayOf(4, 5, 6, 7))
        val i3 = ByteArrayInputStream(byteArrayOf(8, 9))
        val inputs = Collections.enumeration(mutableListOf(i1, i2, i3))
        val stream = SequenceInputStream(inputs)

        read(stream, ByteArray(4)) // wrong
    }

    @Test
    internal fun name2() {
        val i1 = ByteArrayInputStream(byteArrayOf(1, 2, 3))
        val i2 = ByteArrayInputStream(byteArrayOf(4, 5, 6, 7))
        val i3 = ByteArrayInputStream(byteArrayOf(8, 9))
        val inputs = Collections.enumeration(mutableListOf(i1, i2, i3))
        val stream = ImprovedSequenceInputStream(inputs)

        assertThat(read(stream, ByteArray(4)).first).isEqualTo(byteArrayOf(1, 2, 3, 4))
        assertThat(read(stream, ByteArray(2)).first).isEqualTo(byteArrayOf(5, 6))
        assertThat(read(stream, ByteArray(5)).first).isEqualTo(byteArrayOf(7, 8, 9, 0, 0))
    }

    @Test
    internal fun name3() {
        val i1 = ByteArrayInputStream(byteArrayOf(1, 2, 3))
        val i2 = ByteArrayInputStream(byteArrayOf(4, 5, 6, 7))
        val i3 = ByteArrayInputStream(byteArrayOf(8, 9))
        val inputs = Collections.enumeration(mutableListOf(i1, i2, i3))
        val stream = ImprovedSequenceInputStream(inputs)

        assertThat(read(stream, ByteArray(1)).first).isEqualTo(byteArrayOf(1))
        assertThat(read(stream, ByteArray(10)).first).isEqualTo(byteArrayOf(2, 3, 4, 5, 6, 7, 8, 9, 0, 0))
    }

    private fun read(stream: InputStream, array: ByteArray): Pair<ByteArray, Int> {
        val n = stream.read(array)
        println(n)
        println(array.contentToString())
        return array to n
    }
}
