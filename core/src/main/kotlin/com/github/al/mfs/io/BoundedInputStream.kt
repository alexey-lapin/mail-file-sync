package com.github.al.mfs.io

import java.io.IOException
import java.io.InputStream

/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/**
 * This is a stream that will only supply bytes up to a certain length - if its
 * position goes above that, it will stop.
 *
 *
 * This is useful to wrap ServletInputStreams. The ServletInputStream will block
 * if you try to read content from it that isn't there, because it doesn't know
 * whether the content hasn't arrived yet or whether the content has finished.
 * So, one of these, initialized with the Content-length sent in the
 * ServletInputStream's header, will stop it blocking, providing it's been sent
 * with a correct content length.
 *
 * @since 2.0
 */
const val EOF: Int = -1

class BoundedInputStream @JvmOverloads constructor(
    /** the wrapped input stream  */
    private val `in`: InputStream,
    /** the max length to provide  */
    private val max: Long = EOF.toLong()
) : InputStream() {

    /** the number of bytes already returned  */
    private var pos: Long = 0

    /** the marked position  */
    private var mark: Long = EOF.toLong()
    /**
     * Indicates whether the [.close] method
     * should propagate to the underling [InputStream].
     *
     * @return `true` if calling [.close]
     * propagates to the `close()` method of the
     * underlying stream or `false` if it does not.
     */
    /**
     * Set whether the [.close] method
     * should propagate to the underling [InputStream].
     *
     * @param propagateClose `true` if calling
     * [.close] propagates to the `close()`
     * method of the underlying stream or
     * `false` if it does not.
     */
    /** flag if close should be propagated  */
    var isPropagateClose = true

    /**
     * Invokes the delegate's `read()` method if
     * the current position is less than the limit.
     * @return the byte read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    override fun read(): Int {
        if (max >= 0 && pos >= max) {
            return EOF
        }
        val result = `in`.read()
        pos++
        return result
    }

    /**
     * Invokes the delegate's `read(byte[])` method.
     * @param b the buffer to read the bytes into
     * @return the number of bytes read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        return this.read(b, 0, b.size)
    }

    /**
     * Invokes the delegate's `read(byte[], int, int)` method.
     * @param b the buffer to read the bytes into
     * @param off The start offset
     * @param len The number of bytes to read
     * @return the number of bytes read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (max >= 0 && pos >= max) {
            return EOF
        }
        val maxRead = if (max >= 0) Math.min(len.toLong(), max - pos) else len.toLong()
        val bytesRead = `in`.read(b, off, maxRead.toInt())
        if (bytesRead == EOF) {
            return EOF
        }
        pos += bytesRead.toLong()
        return bytesRead
    }

    /**
     * Invokes the delegate's `skip(long)` method.
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        val toSkip = if (max >= 0) Math.min(n, max - pos) else n
        val skippedBytes = `in`.skip(toSkip)
        pos += skippedBytes
        return skippedBytes
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun available(): Int {
        return if (max >= 0 && pos >= max) {
            0
        } else `in`.available()
    }

    /**
     * Invokes the delegate's `toString()` method.
     * @return the delegate's `toString()`
     */
    override fun toString(): String {
        return `in`.toString()
    }

    /**
     * Invokes the delegate's `close()` method
     * if [.isPropagateClose] is `true`.
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    override fun close() {
        if (isPropagateClose) {
            `in`.close()
        }
    }

    /**
     * Invokes the delegate's `reset()` method.
     * @throws IOException if an I/O error occurs
     */
    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        `in`.reset()
        pos = mark
    }

    /**
     * Invokes the delegate's `mark(int)` method.
     * @param readlimit read ahead limit
     */
    @Synchronized
    override fun mark(readlimit: Int) {
        `in`.mark(readlimit)
        mark = pos
    }

    /**
     * Invokes the delegate's `markSupported()` method.
     * @return true if mark is supported, otherwise false
     */
    override fun markSupported(): Boolean {
        return `in`.markSupported()
    }
    /**
     * Creates a new `BoundedInputStream` that wraps the given input
     * stream and limits it to a certain size.
     *
     * @param `in` The wrapped input stream
     * @param max The maximum number of bytes to return
     */
    /**
     * Creates a new `BoundedInputStream` that wraps the given input
     * stream and is unlimited.
     *
     * @param in The wrapped input stream
     */
//    init {
        // Some badly designed methods - eg the servlet API - overload length
        // such that "-1" means stream finished
//        `in` = `in`
//    }
}