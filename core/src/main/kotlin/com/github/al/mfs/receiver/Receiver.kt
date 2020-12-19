package com.github.al.mfs.receiver

object ReceiverFeature {
    const val METADATA_DECRYPT = "receiver.metadata.decrypt"
    const val PAYLOAD_CONTENT_DECOMPRESS = "receiver.payload.content.decompress"
    const val PAYLOAD_CONTENT_DECRYPT = "receiver.payload.content.decrypt"
}

interface Receiver {

    fun <R> receive(context: ReceiverContext<R>) : R

}