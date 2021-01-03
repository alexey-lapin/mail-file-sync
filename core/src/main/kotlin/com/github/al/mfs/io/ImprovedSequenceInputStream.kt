package com.github.al.mfs.io

import java.io.InputStream
import java.io.SequenceInputStream
import java.util.Enumeration

class ImprovedSequenceInputStream<out E : InputStream>(e: Enumeration<E>) : SequenceInputStream(e) {

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        var curOff = off
        var curLen = len
        var total = 0
        var n = super.read(b, off, len)
        total += n
        while (total < len && n > 0) {
            curOff += n
            curLen -= n
            n = super.read(b, curOff, curLen)
            if (n > 0) {
                total += n
            }
        }
        return total
    }
}
