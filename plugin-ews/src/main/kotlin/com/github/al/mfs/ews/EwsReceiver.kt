package com.github.al.mfs.ews

import com.github.al.mfs.receiver.Receiver
import com.github.al.mfs.receiver.ReceiverContext
import microsoft.exchange.webservices.data.core.ExchangeService
import microsoft.exchange.webservices.data.core.PropertySet
import microsoft.exchange.webservices.data.core.enumeration.notification.EventType
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName
import microsoft.exchange.webservices.data.core.service.item.EmailMessage
import microsoft.exchange.webservices.data.core.service.item.Item
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema
import microsoft.exchange.webservices.data.property.complex.FolderId
import microsoft.exchange.webservices.data.property.complex.ItemId
import microsoft.exchange.webservices.data.search.FindItemsResults
import microsoft.exchange.webservices.data.search.ItemView
import microsoft.exchange.webservices.data.search.filter.SearchFilter
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class EwsReceiver(
    private val exchange: ExchangeService
) : Receiver {

    private val map = mutableMapOf<ItemId, EmailMessage>()

    override fun <R> receive(context: ReceiverContext<R>): R {
        search(context)
        if (map.size < context.count) {
            poll(context)
        }
        val receivables = map.map { EwsReceivable(it.value) }
        return context.callback.invoke(receivables)
    }

    private fun search(context: ReceiverContext<*>) {
        val view = ItemView(context.count)
        view.propertySet = PropertySet(BasePropertySet.IdOnly, ItemSchema.Subject)

        val findResults: FindItemsResults<Item> = exchange.findItems(
            WellKnownFolderName.Inbox,
            SearchFilter.ContainsSubstring(ItemSchema.Subject, context.transmissionId),
            view
        )

        exchange.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties)

        findResults.associateByTo(map, { it.id }, { it as EmailMessage })
    }

    private fun poll(context: ReceiverContext<*>) {
        val subscription = exchange.subscribeToPullNotifications(
            listOf(FolderId(WellKnownFolderName.Inbox)),
            5,
            null,
            EventType.NewMail
        )

        while (map.size < context.count) {
            val events = subscription.events
            for (itemEvent in events.itemEvents) {
                if (itemEvent.eventType === EventType.NewMail) {
                    val message = EmailMessage.bind(exchange, itemEvent.itemId)
                    if (!message.subject.startsWith(context.transmissionId)) {
                        continue
                    }
                    map[message.id] = message
                }
            }
            Thread.sleep(10 * 1000)
        }
    }

}