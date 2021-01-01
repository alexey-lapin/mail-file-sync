package com.github.al.mfs.cli.di

import com.github.al.mfs.Crypto
import com.github.al.mfs.SenderFeature
import com.github.al.mfs.TRUE
import com.github.al.mfs.getRandomString
import com.github.al.mfs.io.BoundedRandomCountSplitter
import com.github.al.mfs.io.FixedCountSplitter
import com.github.al.mfs.io.NoopSplitter
import com.github.al.mfs.io.Splitter
import com.github.al.mfs.pipeline.Chunk
import com.github.al.mfs.pipeline.Collector
import com.github.al.mfs.pipeline.DefaultOutputStreamPipeline
import com.github.al.mfs.pipeline.FileChunkCollector
import com.github.al.mfs.pipeline.OutputCompressor
import com.github.al.mfs.pipeline.OutputEncryptor
import com.github.al.mfs.pipeline.OutputEncryptorHeaderWriter
import com.github.al.mfs.pipeline.OutputPipeline
import com.github.al.mfs.pipeline.OutputPipelineMapper
import com.github.al.mfs.sender.DefaultSenderContextFactory
import com.github.al.mfs.sender.EncryptSenderChunkMetadataCustomizer
import com.github.al.mfs.sender.ObfuscateSenderPayloadNameCustomizer
import com.github.al.mfs.sender.OriginalSenderPayloadNameCustomizer
import com.github.al.mfs.sender.PlainStringSenderChunkMetadataCustomizer
import com.github.al.mfs.sender.Sender
import com.github.al.mfs.sender.SenderChunkMetadataCustomizer
import com.github.al.mfs.sender.SenderContextFactory
import com.github.al.mfs.sender.SenderFeatures.METADATA_ENCRYPT
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_CONTENT_COMPRESS
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_CONTENT_ENCRYPT
import com.github.al.mfs.sender.SenderFeatures.PAYLOAD_NAME_OBFUSCATE
import com.github.al.mfs.sender.SenderOrchestrator
import com.github.al.mfs.sender.SenderPayloadNameCustomizer
import com.github.al.mfs.sender.SenderProperties.PAYLOAD_CONTENT_SPLIT_FIXED
import com.github.al.mfs.sender.SenderProperties.PAYLOAD_CONTENT_SPLIT_RANDOM_LOWER
import com.github.al.mfs.sender.SenderProperties.PAYLOAD_CONTENT_SPLIT_RANDOM_UPPER
import com.github.al.mfs.sender.SenderProperties.TRANSMISSION_ID
import com.github.al.mfs.sender.SequentialSenderOrchestrator
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Order
import io.micronaut.core.convert.format.ReadableBytes
import javax.inject.Named
import javax.inject.Singleton

@Factory
class SenderConfig {
    @Requires(missingBeans = [SenderChunkMetadataCustomizer::class])
    @Singleton
    fun defaultChunkMetadataCustomizer(): SenderChunkMetadataCustomizer {
        return PlainStringSenderChunkMetadataCustomizer()
    }

    @Requires(property = METADATA_ENCRYPT, value = TRUE)
    @Singleton
    fun encryptedChunkMetadataCustomizer(crypto: Crypto): SenderChunkMetadataCustomizer {
        return EncryptSenderChunkMetadataCustomizer(crypto)
    }

    @Requires(missingBeans = [SenderPayloadNameCustomizer::class])
    @Singleton
    fun defaultSenderPayloadNameCustomizer(): SenderPayloadNameCustomizer {
        return OriginalSenderPayloadNameCustomizer()
    }

    @Requires(property = PAYLOAD_NAME_OBFUSCATE, value = TRUE)
    @Singleton
    fun obfuscatePayloadNameCustomizer(): SenderPayloadNameCustomizer {
        return ObfuscateSenderPayloadNameCustomizer()
    }

    @Singleton
    fun senderContextFactory(
        @Property(name = TRANSMISSION_ID) transmissionId: String?,
        transmissionMetadataCustomizer: SenderChunkMetadataCustomizer,
        senderPayloadNameCustomizer: SenderPayloadNameCustomizer
    ): SenderContextFactory {
        return DefaultSenderContextFactory(
            transmissionId ?: getRandomString(5),
            transmissionMetadataCustomizer,
            senderPayloadNameCustomizer
        )
    }

    @Prototype
    fun senderOrchestrator(
        sender: Sender,
        pipeline: OutputPipeline<List<Chunk>>,
        factory: SenderContextFactory,
        features: List<SenderFeature>
    ): SenderOrchestrator {
        val featureNames = features.map { it.name }.sorted()
        return SequentialSenderOrchestrator(sender, pipeline, factory, featureNames)
    }
}

@Factory
class OutputPipelineConfig {
    @Named("splitted")
    @Prototype
    fun collector(splitter: Splitter): Collector<List<Chunk>> {
        return FileChunkCollector(splitter)
    }

    @Order(5)
    @Requires(property = PAYLOAD_CONTENT_ENCRYPT, value = TRUE)
    @Singleton
    fun encryptorHeaderWriterOutputMapper(crypto: Crypto): OutputPipelineMapper {
        return OutputEncryptorHeaderWriter(crypto)
    }

    @Order(10)
    @Requires(property = PAYLOAD_CONTENT_COMPRESS, value = TRUE)
    @Singleton
    fun compressorOutputMapper(): OutputPipelineMapper {
        return OutputCompressor()
    }

    @Order(20)
    @Requires(property = PAYLOAD_CONTENT_ENCRYPT, value = TRUE)
    @Singleton
    fun encryptorOutputMapper(crypto: Crypto): OutputPipelineMapper {
        return OutputEncryptor(crypto)
    }

    @Prototype
    fun outputPipeline(
        @Named("splitted") collector: Collector<List<Chunk>>,
        mappers: List<OutputPipelineMapper>
    ): OutputPipeline<List<Chunk>> {
        return DefaultOutputStreamPipeline(collector, mappers)
    }
}

@Factory
class SplitterConfig {
    @Requires(missingBeans = [Splitter::class])
    @Singleton
    fun defaultSplitter(): Splitter {
        return NoopSplitter()
    }

    @Requires(property = PAYLOAD_CONTENT_SPLIT_FIXED)
    @Prototype
    fun fixedCountSplitter(
        @Property(name = PAYLOAD_CONTENT_SPLIT_FIXED) @ReadableBytes fixedLimit: Long
    ): Splitter {
        return FixedCountSplitter(fixedLimit)
    }

    @Requirements(
        Requires(property = PAYLOAD_CONTENT_SPLIT_RANDOM_LOWER),
        Requires(property = PAYLOAD_CONTENT_SPLIT_RANDOM_UPPER)
    )
    @Prototype
    fun randomCountSplitter(
        @Property(name = PAYLOAD_CONTENT_SPLIT_RANDOM_LOWER) @ReadableBytes lower: Long,
        @Property(name = PAYLOAD_CONTENT_SPLIT_RANDOM_UPPER) @ReadableBytes upper: Long
    ): Splitter {
        return BoundedRandomCountSplitter(lower, upper)
    }
}
