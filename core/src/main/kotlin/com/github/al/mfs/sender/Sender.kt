package com.github.al.mfs.sender

object SenderFeature {
    const val METADATA_ENCRYPT = "sender.metadata.encrypt"
    const val PAYLOAD_CONTENT_COMPRESS = "sender.payload.content.compress"
    const val PAYLOAD_CONTENT_ENCRYPT = "sender.payload.content.encrypt"
    const val PAYLOAD_CONTENT_SPLIT_FIXED = "sender.payload.content.split.fixed"
    const val PAYLOAD_CONTENT_SPLIT_RANDOM_LOWER = "sender.payload.content.split.random.lower"
    const val PAYLOAD_CONTENT_SPLIT_RANDOM_UPPER = "sender.payload.content.split.random.upper"
    const val PAYLOAD_NAME_OBFUSCATE = "sender.payload.name.obfuscate"
    const val RECIPIENTS = "sender.recipients"
}

interface Sender {

    var recipients: List<String>

    fun send(context: SenderContext)
}
