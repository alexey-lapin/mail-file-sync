package com.github.al.mfs.cli

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.Option
import com.github.ajalt.clikt.sources.ValueSource
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

class PropertiesValueSource(
    filePath: String,
    private val getKey: (Context, Option) -> String = ValueSource.getKey(joinSubcommands = ".")
) : ValueSource {

    private val values: Map<String, String> = try {
        val props = Properties()
        props.load(FileInputStream(filePath))
        props.asSequence().associate { (k, v) -> k.toString() to v.toString() }
    } catch (e: IOException) {
        mapOf()
    }

    override fun getValues(context: Context, option: Option): List<ValueSource.Invocation> {
        val key = option.valueSourceKey ?: getKey(context, option)
        val value = values[key]?.let { ValueSource.Invocation.just(it) }.orEmpty()
        return value
    }
}
