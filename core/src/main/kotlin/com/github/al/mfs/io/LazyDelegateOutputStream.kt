package com.github.al.mfs.io

import java.io.OutputStream

class LazyDelegateOutputStream : OutputStream() {

    lateinit var delegate: OutputStream

    override fun write(b: Int) {
        delegate.write(b)
    }

    override fun write(b: ByteArray) {
        delegate.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        delegate.write(b, off, len)
    }

    override fun close() {
        delegate.close()
    }

    override fun flush() {
        delegate.flush()
    }
}
