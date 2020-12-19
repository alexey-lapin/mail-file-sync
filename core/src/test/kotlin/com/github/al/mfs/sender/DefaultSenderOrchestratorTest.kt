package com.github.al.mfs.sender

import com.github.al.mfs.sender.Sender
import com.github.al.mfs.sender.SenderContext
import mu.KotlinLogging


class FakeSender(override var recipients: List<String> = listOf()) : Sender {

    private val logger = KotlinLogging.logger {}

    override fun send(context: SenderContext) {
        logger.info { "sssend" }
    }

}