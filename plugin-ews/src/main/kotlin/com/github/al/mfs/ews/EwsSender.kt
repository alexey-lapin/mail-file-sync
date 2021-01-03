package com.github.al.mfs.ews

import com.github.al.mfs.sender.Sender
import com.github.al.mfs.sender.SenderContext
import microsoft.exchange.webservices.data.core.ExchangeService
import microsoft.exchange.webservices.data.core.service.item.EmailMessage
import mu.KotlinLogging
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class EwsSender(private val exchange: ExchangeService) : Sender {

    override var recipients: List<String> = listOf()

    override fun send(context: SenderContext) {
        val message = EmailMessage(exchange)
        message.subject = context.subject
        message.attachments.addFileAttachment(context.attachmentName, context.attachmentContent)
        recipients.forEach {
            message.toRecipients.add(it)
        }
        logger.info { "${context.metadata.marker} send started" }
        val duration = Duration.ofMillis(measureTimeMillis { message.send() })
        logger.info { "${context.metadata.marker} send finished: ${duration.toString().substring(2)}" }
    }
}
