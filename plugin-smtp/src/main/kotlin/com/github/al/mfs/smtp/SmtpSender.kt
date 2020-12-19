package com.github.al.mfs.smtp

import com.github.al.mfs.sender.Sender
import com.github.al.mfs.sender.SenderContext
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class SmtpSender(private val session: Session, override var recipients: List<String>) : Sender {

    override fun send(context: SenderContext) {
        val message = MimeMessage(session)

        recipients.forEach { recipient ->
            message.addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
        }
        message.subject = context.subject

        val messageBodyPart = MimeBodyPart()
        val source = ByteArrayDataSource(context.attachmentContent, "application/octet-stream")
        messageBodyPart.dataHandler = DataHandler(source)
        messageBodyPart.fileName = context.attachmentName

        val multipart = MimeMultipart()
        multipart.addBodyPart(messageBodyPart)

        message.setContent(multipart)

        Transport.send(message)
    }

}

