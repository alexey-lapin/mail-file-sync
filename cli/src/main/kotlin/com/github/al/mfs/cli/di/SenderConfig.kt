package com.github.al.mfs.cli.di

import com.github.al.mfs.Crypto
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
import com.github.al.mfs.pipeline.OutputPipeline
import com.github.al.mfs.pipeline.OutputPipelineMapper
import com.github.al.mfs.sender.ChunkSenderPayloadNameCustomizer
import com.github.al.mfs.sender.DefaultSenderContextFactory
import com.github.al.mfs.sender.EncryptSenderChunkMetadataCustomizer
import com.github.al.mfs.sender.ObfuscateSenderPayloadNameCustomizer
import com.github.al.mfs.sender.Sender
import com.github.al.mfs.sender.SenderChunkMetadataCustomizer
import com.github.al.mfs.sender.SenderContextFactory
import com.github.al.mfs.sender.SenderFeature
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_COMPRESS
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_ENCRYPT
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_SPLIT_FIXED
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_SPLIT_RANDOM_LOWER
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_SPLIT_RANDOM_UPPER
import com.github.al.mfs.sender.SenderOrchestrator
import com.github.al.mfs.sender.SenderPayloadNameCustomizer
import com.github.al.mfs.sender.SequentialSenderOrchestrator
import com.github.al.mfs.sender.StringSenderChunkMetadataCustomizer
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
        return StringSenderChunkMetadataCustomizer()
    }

    @Requires(property = SenderFeature.METADATA_ENCRYPT)
    @Singleton
    fun encryptedChunkMetadataCustomizer(crypto: Crypto): SenderChunkMetadataCustomizer {
        return EncryptSenderChunkMetadataCustomizer(crypto)
    }

    @Requires(missingBeans = [SenderPayloadNameCustomizer::class])
    @Singleton
    fun defaultSenderPayloadNameCustomizer(): SenderPayloadNameCustomizer {
        return ChunkSenderPayloadNameCustomizer()
    }

    @Requires(property = SenderFeature.PAYLOAD_NAME_OBFUSCATE)
    @Singleton
    fun obfuscatePayloadNameCustomizer(): SenderPayloadNameCustomizer {
        return ObfuscateSenderPayloadNameCustomizer()
    }

    @Singleton
    fun senderContextFactory(
        @Property(name = "transmission.id") transmissionId: String?,
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
        factory: SenderContextFactory
    ): SenderOrchestrator {
        return SequentialSenderOrchestrator(sender, pipeline, factory)
    }
}

@Factory
class OutputPipelineConfig {
    @Named("splitted")
    @Prototype
    fun collector(splitter: Splitter): Collector<List<Chunk>> {
        return FileChunkCollector(splitter)
    }

    @Order(10)
    @Requires(property = PAYLOAD_CONTENT_COMPRESS)
    @Singleton
    fun compressorOutputMapper(): OutputPipelineMapper {
        return OutputCompressor()
    }

    @Order(20)
    @Requires(property = PAYLOAD_CONTENT_ENCRYPT)
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
