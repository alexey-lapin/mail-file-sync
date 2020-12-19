package com.github.al.mfs.receiver

interface ReceiverContext<R> {
    val transmissionId: String
    val count: Int
    val callback: (List<Receivable>) -> R
}

data class SimpleReceiverContext<R>(
    override val transmissionId: String,
    override val count: Int,
    override val callback: (List<Receivable>) -> R
) : ReceiverContext<R>