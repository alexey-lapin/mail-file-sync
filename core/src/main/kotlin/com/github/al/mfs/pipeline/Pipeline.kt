package com.github.al.mfs.pipeline

import com.github.al.mfs.ReceiverFeature
import com.github.al.mfs.SenderFeature
import mu.KotlinLogging
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Named

private val logger = KotlinLogging.logger {}

interface Pipeline<R, T> : Closeable {
    fun addMapper(mapper: (T) -> T): Pipeline<R, T>
    fun process(input: Input): R
}

interface InputPipeline<R> : Pipeline<R, InputStream>

interface OutputPipeline<R> : Pipeline<R, OutputStream>

interface InputPipelineMapper : SenderFeature, ReceiverFeature, (InputStream) -> InputStream

interface OutputPipelineMapper : SenderFeature, ReceiverFeature, (OutputStream) -> OutputStream

class DefaultInputStreamPipeline<R>(
    private val collector: Collector<R>,
    mappers: List<(InputStream) -> InputStream> = mutableListOf()
) : InputPipeline<R> {

    private val mappers: MutableList<(InputStream) -> InputStream> = mutableListOf()

    init {
        this.mappers.addAll(mappers)
    }

    override fun addMapper(mapper: (InputStream) -> InputStream): DefaultInputStreamPipeline<R> {
        mappers.add(mapper)
        return this
    }

    override fun process(input: Input): R {
        val chain = mutableListOf<Any>()
        var currentInput = input.inputStream
        chain.add(currentInput)
        for (mapper in mappers) {
            currentInput = mapper.invoke(currentInput)
            chain.add(currentInput)
        }
        val output = collector.getSink(input)
        chain.add(output)
        logger.info { "pipeline: " + chain.joinToString(separator = " -> ") { it.javaClass.name } }

        currentInput.use { usedInput ->
            output.use { usedOutput ->
                return collector.collect(InputStreamInput(input.name, usedInput), usedOutput)
            }
        }
    }

    override fun close() {
        collector.close()
    }
}

class DefaultOutputStreamPipeline<R>(
    private val collector: Collector<R>,
    mappers: List<(OutputStream) -> OutputStream> = mutableListOf()
) : OutputPipeline<R> {

    private val mappers: MutableList<(OutputStream) -> OutputStream> = mutableListOf()

    init {
        this.mappers.addAll(mappers)
    }

    override fun addMapper(mapper: (OutputStream) -> OutputStream): DefaultOutputStreamPipeline<R> {
        mappers.add(mapper)
        return this
    }

    override fun process(input: Input): R {
        val chain = mutableListOf<Any>()
        var currentOutput = collector.getSink(input)
        chain.add(currentOutput)
        for (mapper in mappers) {
            currentOutput = mapper.invoke(currentOutput)
            chain.add(currentOutput)
        }
        chain.add(input)
        logger.info { "pipeline: " + chain.reversed().joinToString(separator = " -> ") { it.javaClass.name } }

        input.inputStream.use {
            currentOutput.use { usedOutput ->
                return collector.collect(input, usedOutput)
            }
        }
    }

    override fun close() {
        collector.close()
    }
}
