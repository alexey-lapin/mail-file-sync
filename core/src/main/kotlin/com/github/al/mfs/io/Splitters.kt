package com.github.al.mfs.io

import kotlin.random.Random

interface Splitter {
    fun getLimit(): Long

    fun shouldSplit(b: Int): Boolean

    fun update(b: Int)

    fun shouldSplit(b: ByteArray, off: Int, len: Int): Boolean

    fun split(b: ByteArray, off: Int, len: Int): Int

    fun update(b: ByteArray, off: Int, len: Int)

    fun reset()
}

class NoopSplitter : Splitter {
    override fun getLimit() = Long.MAX_VALUE

    override fun shouldSplit(b: Int): Boolean = false

    override fun update(b: Int) {}

    override fun shouldSplit(b: ByteArray, off: Int, len: Int): Boolean = false

    override fun split(b: ByteArray, off: Int, len: Int): Int = 0

    override fun update(b: ByteArray, off: Int, len: Int) {}

    override fun reset() {}
}

abstract class AbstractCountSplitter(protected val limitProvider: () -> Long) : Splitter {
    private var count: Long = 0
    protected var lim = limitProvider.invoke()

    override fun getLimit() = lim

    override fun shouldSplit(b: Int): Boolean {
        return count + 1 > lim
    }

    override fun update(b: Int) {
        count++
    }

    override fun shouldSplit(b: ByteArray, off: Int, len: Int): Boolean {
        return count + len > lim
    }

    override fun split(b: ByteArray, off: Int, len: Int): Int {
        return (lim - count).toInt()
    }

    override fun update(b: ByteArray, off: Int, len: Int) {
        count += len
    }

    override fun reset() {
        count = 0
    }

    override fun toString() = "${this::class.java.simpleName}[limit:$lim count:$count]"
}

class FixedCountSplitter(private val fixedLimit: Long) :
    AbstractCountSplitter({ fixedLimit })

class BoundedRandomCountSplitter(lower: Long, upper: Long) :
    AbstractCountSplitter({ Random.nextLong(lower, upper) }) {

    init {
        require(lower < upper) { "failed: lower should be less than upper" }
    }

    override fun reset() {
        super.reset()
        lim = limitProvider.invoke()
    }
}
