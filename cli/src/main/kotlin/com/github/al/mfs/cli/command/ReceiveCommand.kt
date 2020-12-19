package com.github.al.mfs.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.groups.required
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.al.mfs.receiver.ReceiverFeature.PAYLOAD_CONTENT_DECOMPRESS
import com.github.al.mfs.receiver.ReceiverOrchestrator
import io.micronaut.context.ApplicationContext

class ReceiveCommand() : CliktCommand() {

    private val transportOptions by option("--transport").groupChoice(
        "ews" to EwsOptions(),
        // imap
    ).required()

    private val decryptOptions by DecryptOptions().cooccurring()

    private val contentDecompress by option("--content-decompress", valueSourceKey = PAYLOAD_CONTENT_DECOMPRESS).flag()

    private val transmissionId by argument()

    override fun run() {
        ApplicationContext.run(context()).use { ctx ->
            val orchestrator = ctx.getBean(ReceiverOrchestrator::class.java)
            orchestrator.receive(transmissionId)
        }
    }
}
