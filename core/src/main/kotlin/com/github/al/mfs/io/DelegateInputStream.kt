package com.github.al.mfs.io

import java.io.InputStream

class DelegateInputStream(
    private val delegate: InputStream,
    initializer: (InputStream) -> Unit
) : InputStream() {

    init {
        initializer.invoke(delegate)
    }

    override fun close() {
        delegate.close()
    }

    override fun read(): Int {
        return delegate.read()
    }

    override fun read(b: ByteArray): Int {
        return delegate.read(b)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return delegate.read(b, off, len)
    }

    override fun skip(n: Long): Long {
        return delegate.skip(n)
    }

    override fun available(): Int {
        return delegate.available()
    }

    override fun mark(readlimit: Int) {
        delegate.mark(readlimit)
    }

    override fun reset() {
        delegate.reset()
    }

    override fun markSupported(): Boolean {
        return delegate.markSupported()
    }
}
