package com.github.al.mfs.ews

import com.github.al.mfs.receiver.Receivable
import microsoft.exchange.webservices.data.core.service.item.EmailMessage
import microsoft.exchange.webservices.data.property.complex.Attachment
import microsoft.exchange.webservices.data.property.complex.FileAttachment
import microsoft.exchange.webservices.data.property.complex.ItemAttachment
import java.io.OutputStream

class EwsReceivable (private val message: EmailMessage) : Receivable {

    override fun getSubject(): String = message.subject

    override fun loadAttachment(output: OutputStream) {
        val attachment = message.attachments.items.elementAtOrNull(0)
        findFileAttachment(attachment)?.load(output)
    }

    private fun findFileAttachment(attachment: Attachment?): FileAttachment? {
        return when (attachment) {
            is FileAttachment -> attachment
            is ItemAttachment -> {
                attachment.load()
                findFileAttachment(attachment.item.attachments.items[0])
            }
            else -> null
        }
    }

}