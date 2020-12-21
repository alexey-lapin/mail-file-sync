package com.github.al.mfs.cli.di

import com.github.al.mfs.Crypto
import com.github.al.mfs.ReceiverFeature
import com.github.al.mfs.TRUE
import com.github.al.mfs.pipeline.Collector
import com.github.al.mfs.pipeline.DefaultInputStreamPipeline
import com.github.al.mfs.pipeline.FileCollector
import com.github.al.mfs.pipeline.InputDecompressor
import com.github.al.mfs.pipeline.InputDecryptor
import com.github.al.mfs.pipeline.InputPipeline
import com.github.al.mfs.pipeline.InputPipelineMapper
import com.github.al.mfs.receiver.DecryptReceiverChunkMetadataCustomizer
import com.github.al.mfs.receiver.PlainStringChunkMetadataCustomizer
import com.github.al.mfs.receiver.Receiver
import com.github.al.mfs.receiver.ReceiverChunkMetadataCustomizer
import com.github.al.mfs.receiver.ReceiverFeatures.METADATA_DECRYPT
import com.github.al.mfs.receiver.ReceiverFeatures.PAYLOAD_CONTENT_DECOMPRESS
import com.github.al.mfs.receiver.ReceiverFeatures.PAYLOAD_CONTENT_DECRYPT
import com.github.al.mfs.receiver.ReceiverOrchestrator
import com.github.al.mfs.receiver.SequentialReceiverOrchestrator
import com.github.al.mfs.receiver.callback.DefaultInitializer
import com.github.al.mfs.receiver.callback.Initializer
import com.github.al.mfs.receiver.callback.Merger
import com.github.al.mfs.receiver.callback.TempFileMerger
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Order
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Factory
class ReceiverConfig {
    @Requires(missingBeans = [ReceiverChunkMetadataCustomizer::class])
    @Singleton
    fun defaultReceiverChunkMetadataCustomizer(): ReceiverChunkMetadataCustomizer {
        return PlainStringChunkMetadataCustomizer()
    }

    @Requires(property = METADATA_DECRYPT, value = TRUE)
    @Singleton
    fun decryptReceiverChunkMetadataCustomizer(crypto: Crypto): ReceiverChunkMetadataCustomizer {
        return DecryptReceiverChunkMetadataCustomizer(crypto)
    }

    @Singleton
    fun receiverOrchestrator(
        receiver: Receiver,
        initializer: Initializer,
        merger: Merger,
        features: List<ReceiverFeature>
    ): ReceiverOrchestrator {
        val featureNames = features.map { it.name }.sorted()
        return SequentialReceiverOrchestrator(receiver, initializer, merger, featureNames)
    }

    @Singleton
    fun defaultInitializer(receiverChunkMetadataCustomizer: ReceiverChunkMetadataCustomizer): Initializer {
        return DefaultInitializer(receiverChunkMetadataCustomizer)
    }

    @Singleton
    fun defaultMerger(
        receiverChunkMetadataCustomizer: ReceiverChunkMetadataCustomizer,
        pipeline: InputPipeline<File>
    ): Merger {
        return TempFileMerger(receiverChunkMetadataCustomizer, pipeline)
    }
}

@Factory
class InputPipelineConfig {
    @Named("merged")
    @Singleton
    fun collectorFile(): Collector<File> {
        return FileCollector()
    }

    @Requires(property = PAYLOAD_CONTENT_DECOMPRESS, value = TRUE)
    @Order(10)
    @Singleton
    fun decompressorInputMapper(): InputPipelineMapper {
        return InputDecompressor()
    }

    @Requires(property = PAYLOAD_CONTENT_DECRYPT, value = TRUE)
    @Order(20)
    @Singleton
    fun decryptorInputMapper(crypto: Crypto): InputPipelineMapper {
        return InputDecryptor(crypto)
    }

    @Singleton
    fun inputPipeline(
        @Named("merged") collector: Collector<File>,
        mappers: List<InputPipelineMapper>
    ): InputPipeline<File> {
        return DefaultInputStreamPipeline(collector, mappers)
    }
}
