package com.github.al.mfs.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.groups.required
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_COMPRESS
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_SPLIT_FIXED
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_NAME_OBFUSCATE
import com.github.al.mfs.sender.SenderFeature.RECIPIENTS
import com.github.al.mfs.sender.SenderOrchestrator
import io.micronaut.context.ApplicationContext

class SendCommand : CliktCommand() {

    private val recipients by option("--recipient", valueSourceKey = RECIPIENTS).required()

    private val transportOptions by option("--transport").groupChoice(
        "ews" to EwsOptions(),
        "smtp" to SmtpOptions()
    ).required()

    private val encryptOptions by EncryptOptions().cooccurring()

    private val contentSplitFixed by option("--content-split", valueSourceKey = PAYLOAD_CONTENT_SPLIT_FIXED)
    private val contentCompress by option("--content-compress", valueSourceKey = PAYLOAD_CONTENT_COMPRESS).flag()
    private val nameObfuscate by option("--name-obfuscate", valueSourceKey = PAYLOAD_NAME_OBFUSCATE).flag()

    private val files by argument("files").file(mustExist = true, canBeDir = false).multiple(true)

    override fun run() {
        ApplicationContext.run(context()).use { ctx ->
            val orchestrator = ctx.getBean(SenderOrchestrator::class.java)
            orchestrator.send(files[0])
        }
    }
}
