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

package com.github.al.mfs.io

import java.io.InputStream

const val EOF: Int = -1

// This is a Kotlin port of org.apache.commons.io.input.BoundedInputStream class
// from apache commons-io

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
 */
class BoundedInputStream(
    private val input: InputStream,
    private val max: Long = EOF.toLong()
) : InputStream() {

    private var pos: Long = 0

    private var mark: Long = EOF.toLong()

    var isPropagateClose = true

    override fun read(): Int {
        if (max in 0..pos) {
            return EOF
        }
        val result = input.read()
        pos++
        return result
    }

    override fun read(b: ByteArray): Int {
        return this.read(b, 0, b.size)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (max in 0..pos) {
            return EOF
        }
        val maxRead = if (max >= 0) Math.min(len.toLong(), max - pos) else len.toLong()
        val bytesRead = input.read(b, off, maxRead.toInt())
        if (bytesRead == EOF) {
            return EOF
        }
        pos += bytesRead.toLong()
        return bytesRead
    }

    override fun skip(n: Long): Long {
        val toSkip = if (max >= 0) Math.min(n, max - pos) else n
        val skippedBytes = input.skip(toSkip)
        pos += skippedBytes
        return skippedBytes
    }

    override fun available(): Int {
        return if (max in 0..pos) 0 else input.available()
    }

    override fun toString(): String {
        return input.toString()
    }

    override fun close() {
        if (isPropagateClose) {
            input.close()
        }
    }

    @Synchronized
    override fun reset() {
        input.reset()
        pos = mark
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        input.mark(readlimit)
        mark = pos
    }

    override fun markSupported(): Boolean {
        return input.markSupported()
    }
}
