package com.github.al.mfs.pipeline

import com.github.al.mfs.ReceiverFeature
import com.github.al.mfs.SenderFeature
import mu.KotlinLogging
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

private val logger = KotlinLogging.logger {}

interface Pipeline<R, T> : Closeable {
    val context: MutableMap<String, Any>
    fun addMapper(mapper: Mapper<T>): Pipeline<R, T>
    fun process(input: Input): R
}

typealias Mapper<T> = (T, Pipeline<*, T>) -> T

interface InputPipeline<R> : Pipeline<R, InputStream>

interface OutputPipeline<R> : Pipeline<R, OutputStream>

interface InputPipelineMapper : SenderFeature, ReceiverFeature, Mapper<InputStream>

interface OutputPipelineMapper : SenderFeature, ReceiverFeature, Mapper<OutputStream>

class DefaultInputStreamPipeline<R>(
    private val collector: Collector<R>,
    mappers: List<Mapper<InputStream>> = mutableListOf()
) : InputPipeline<R> {

    override val context: MutableMap<String, Any> = mutableMapOf()
    private val mappers: MutableList<Mapper<InputStream>> = mutableListOf()

    init {
        this.mappers.addAll(mappers)
    }

    override fun addMapper(mapper: Mapper<InputStream>): DefaultInputStreamPipeline<R> {
        mappers.add(mapper)
        return this
    }

    override fun process(input: Input): R {
        val chain = mutableListOf<Any>()
        var currentInput = input.inputStream
        chain.add(currentInput)
        for (mapper in mappers) {
            currentInput = mapper.invoke(currentInput, this)
            chain.add(currentInput)
        }
        val output = collector.getSink(input)
        chain.add(output)
        logger.info { "pipeline: " + chain.toSet().joinToString(separator = " -> ") { it.javaClass.name } }

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
    mappers: List<Mapper<OutputStream>> = mutableListOf()
) : OutputPipeline<R> {

    override val context: MutableMap<String, Any> = mutableMapOf()
    private val mappers: MutableList<Mapper<OutputStream>> = mutableListOf()

    init {
        this.mappers.addAll(mappers)
    }

    override fun addMapper(mapper: Mapper<OutputStream>): DefaultOutputStreamPipeline<R> {
        mappers.add(mapper)
        return this
    }

    override fun process(input: Input): R {
        val chain = mutableListOf<Any>()
        var currentOutput = collector.getSink(input)
        chain.add(currentOutput)
        for (mapper in mappers) {
            currentOutput = mapper.invoke(currentOutput, this)
            chain.add(currentOutput)
        }
        chain.add(input)
        logger.info { "pipeline: " + chain.reversed().toSet().joinToString(separator = " -> ") { it.javaClass.name } }

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
