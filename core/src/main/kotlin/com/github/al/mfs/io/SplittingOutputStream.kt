package com.github.al.mfs.io

import java.io.OutputStream

class SplittingOutputStream(
    private val splitter: Splitter,
    private val factory: (Int) -> OutputStream
) : OutputStream() {

    var index = 0
    private var out: OutputStream = factory.invoke(index)

    override fun write(b: Int) {
        if (splitter.shouldSplit(b)) {
            next()
            write(b)
        } else {
            splitter.update(b)
            out.write(b)
        }
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (splitter.shouldSplit(b, off, len)) {
            val splitted = splitter.split(b, off, len)
            splitter.update(b, off, splitted)
            out.write(b, off, splitted)
            next()
            write(b, off + splitted, len - splitted)
        } else {
            splitter.update(b, off, len)
            out.write(b, off, len)
        }
    }

    private fun next() {
        out.flush()
        out.close()
        out = factory.invoke(++index)
        splitter.reset()
    }

    override fun close() {
        out.close()
    }

    override fun flush() {
        out.flush()
    }
}
