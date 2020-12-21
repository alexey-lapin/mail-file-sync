package com.github.al.mfs.receiver

import com.github.al.mfs.receiver.callback.Initializer
import com.github.al.mfs.receiver.callback.Merger
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface ReceiverOrchestrator {

    fun receive(transmissionId: String)
}

class SequentialReceiverOrchestrator(
    private val receiver: Receiver,
    private val initializer: Initializer,
    private val merger: Merger,
    private val features: List<String>
) : ReceiverOrchestrator {

    override fun receive(transmissionId: String) {
        logger.info { "applied features: $features" }
        val metadata = receiver.receive(SimpleReceiverContext(transmissionId, 1, initializer))
        receiver.receive((SimpleReceiverContext(transmissionId, metadata.marker.total, merger)))
    }
}
