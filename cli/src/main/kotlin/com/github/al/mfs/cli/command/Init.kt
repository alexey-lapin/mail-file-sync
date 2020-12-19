package com.github.al.mfs.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.groups.required
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.al.mfs.cli.MailFileSync
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_COMPRESS
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_CONTENT_SPLIT_FIXED
import com.github.al.mfs.sender.SenderFeature.PAYLOAD_NAME_OBFUSCATE
import com.github.al.mfs.sender.SenderFeature.RECIPIENTS
import java.io.File

class Init : CliktCommand() {

    private val recipients by option("--recipient", valueSourceKey = RECIPIENTS).required()

    private val transportOptions by option("--transport").groupChoice(
        "ews" to EwsOptions(),
        "smtp" to SmtpOptions()
    ).required()

    private val encryptOptions by EncryptOptions().cooccurring()

    private val contentSplitFixed by option("--content-split", valueSourceKey = PAYLOAD_CONTENT_SPLIT_FIXED)
    private val contentCompress by option("--content-compress", valueSourceKey = PAYLOAD_CONTENT_COMPRESS).flag()
    private val nameObfuscate by option("--name-obfuscate", valueSourceKey = PAYLOAD_NAME_OBFUSCATE).flag()

    private val files = listOf(File(MailFileSync::class.java.protectionDomain.codeSource.location.toURI().path))

    override fun run() {
        println("initializing $files")
    }

}