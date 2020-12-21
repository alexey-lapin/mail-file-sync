package com.github.al.mfs.receiver

object ReceiverFeatures {
    const val METADATA_DECRYPT = "receiver.metadata.decrypt"
    const val METADATA_NAMED = "receiver.metadata.named"
    const val METADATA_PLAIN = "receiver.metadata.plain"
    const val PAYLOAD_CONTENT_DECOMPRESS = "receiver.payload.content.decompress"
    const val PAYLOAD_CONTENT_DECRYPT = "receiver.payload.content.decrypt"
}

interface Receiver {
    fun <R> receive(context: ReceiverContext<R>): R
}
