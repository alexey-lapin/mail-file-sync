package com.github.al.mfs.receiver

import com.github.al.mfs.receiver.callback.Initializer
import com.github.al.mfs.receiver.callback.Merger

interface ReceiverOrchestrator {

    fun receive(transmissionId: String)

}

class SequentialReceiverOrchestrator(
    private val receiver: Receiver,
    private val initializer: Initializer,
    private val merger: Merger
) : ReceiverOrchestrator {

    override fun receive(transmissionId: String) {
        val metadata = receiver.receive(SimpleReceiverContext(transmissionId, 1, initializer))
        receiver.receive((SimpleReceiverContext(transmissionId, metadata.marker.total, merger)))
    }

}

