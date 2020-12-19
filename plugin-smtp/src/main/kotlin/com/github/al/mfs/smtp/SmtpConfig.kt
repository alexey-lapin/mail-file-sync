package com.github.al.mfs.smtp

import com.github.al.mfs.sender.Sender
import com.github.al.mfs.smtp.SmtpProperties.SMTP_HOST
import com.github.al.mfs.smtp.SmtpProperties.SMTP_PASS
import com.github.al.mfs.smtp.SmtpProperties.SMTP_PORT
import com.github.al.mfs.smtp.SmtpProperties.SMTP_USER
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import java.util.*
import javax.inject.Singleton
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session

object SmtpProperties {
    const val SMTP_HOST = "smtp.host"
    const val SMTP_PORT = "smtp.port"
    const val SMTP_USER = "smtp.user"
    const val SMTP_PASS = "smtp.pass"
}

@Requirements(
    Requires(property = SMTP_HOST),
    Requires(property = SMTP_PORT),
    Requires(property = SMTP_USER),
    Requires(property = SMTP_PASS)
)
@Factory
class SmtpConfig {

    @Singleton
    fun authenticator(
        @Property(name = SMTP_USER) user: String,
        @Property(name = SMTP_PASS) pass: String
    ): Authenticator {
        return object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(user, pass)
            }
        }
    }

    @Requires(beans = [Authenticator::class])
    @Singleton
    fun session(
        @Property(name = SMTP_HOST) host: String,
        @Property(name = SMTP_PORT) port: String,
        authenticator: Authenticator
    ): Session {
        val props = Properties()
        props["mail.smtp.auth"] = true
        props["mail.smtp.starttls.enable"] = true
        props["mail.smtp.host"] = host
        props["mail.smtp.port"] = port
        return Session.getInstance(props, authenticator)
    }

    @Requires(beans = [Session::class])
    fun smtpSender(
        @Property(name = "sender.recipients") recipients: List<String>,
        session: Session
    ): Sender {
        return SmtpSender(session, recipients)
    }

}
