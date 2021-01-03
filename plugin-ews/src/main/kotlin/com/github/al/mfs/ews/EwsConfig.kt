package com.github.al.mfs.ews

import com.github.al.mfs.ews.EwsProperties.EWS_PASS
import com.github.al.mfs.ews.EwsProperties.EWS_URL
import com.github.al.mfs.ews.EwsProperties.EWS_USER
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import microsoft.exchange.webservices.data.core.ExchangeService
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion
import microsoft.exchange.webservices.data.credential.WebCredentials
import java.net.URI

object EwsProperties {
    const val EWS_URL = "ews.url"
    const val EWS_USER = "ews.user"
    const val EWS_PASS = "ews.pass"
}

@Requirements(
    Requires(property = EWS_URL),
    Requires(property = EWS_USER),
    Requires(property = EWS_PASS)
)
@Factory
class EwsConfig {

    @Prototype
    fun exchangeService(
        @Property(name = EWS_URL) url: URI,
        @Property(name = EWS_USER) username: String,
        @Property(name = EWS_PASS) password: String
    ): ExchangeService {
        val exchange = ExchangeService(ExchangeVersion.Exchange2010_SP2)
        exchange.url = url
        exchange.credentials = WebCredentials(username, password)
        return exchange
    }

    @Requires(beans = [ExchangeService::class])
    @Prototype
    fun ewsSender(
        exchange: ExchangeService,
        @Property(name = "sender.recipients") recipients: List<String>
    ): EwsSender {
        val sender = EwsSender(exchange)
        sender.recipients = recipients
        return sender
    }

    @Requires(beans = [ExchangeService::class])
    @Prototype
    fun ewsReceiver(exchange: ExchangeService): EwsReceiver {
        return EwsReceiver(exchange)
    }
}
