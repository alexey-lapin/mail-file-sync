package com.github.al.mfs.io

import java.io.OutputStream

class DelegateOutputStream(
    private val delegate: OutputStream,
    initializer: (OutputStream) -> Unit
) : OutputStream() {

    init {
        initializer.invoke(delegate)
    }

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
