package com.github.al.mfs.sender

import mu.KotlinLogging

class FakeSender(override var recipients: List<String> = listOf()) : Sender {

    private val logger = KotlinLogging.logger {}

    override fun send(context: SenderContext) {
        logger.info { "sssend" }
    }
}
