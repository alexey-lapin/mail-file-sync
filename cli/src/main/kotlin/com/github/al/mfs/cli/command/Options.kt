package com.github.al.mfs.cli.command

import com.github.ajalt.clikt.core.Abort
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.required
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionDelegate
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.transformAll
import com.github.al.mfs.ews.EwsProperties.EWS_PASS
import com.github.al.mfs.ews.EwsProperties.EWS_URL
import com.github.al.mfs.ews.EwsProperties.EWS_USER
import com.github.al.mfs.receiver.ReceiverFeature.METADATA_DECRYPT
import com.github.al.mfs.receiver.ReceiverFeature.PAYLOAD_CONTENT_DECRYPT
import com.github.al.mfs.sender.SenderFeature
import com.github.al.mfs.smtp.SmtpProperties.SMTP_HOST
import com.github.al.mfs.smtp.SmtpProperties.SMTP_PASS
import com.github.al.mfs.smtp.SmtpProperties.SMTP_PORT
import com.github.al.mfs.smtp.SmtpProperties.SMTP_USER

open class CryptOptions : OptionGroup() {
    val passphrase by option("--passphrase", valueSourceKey = "crypto.passphrase").requiredPrompt(hideInput = true)
}

class EncryptOptions : CryptOptions() {
    val metadataEncrypt by option("--metadata-encrypt", valueSourceKey = SenderFeature.METADATA_ENCRYPT).flag()
    val payloadContentEncrypt by option(
        "--content-encrypt",
        valueSourceKey = SenderFeature.PAYLOAD_CONTENT_ENCRYPT
    ).flag()
}

class DecryptOptions : CryptOptions() {
    val metadataDecrypt by option("--metadata-decrypt", valueSourceKey = METADATA_DECRYPT).flag()
    val payloadContentDecrypt by option("--content-decrypt", valueSourceKey = PAYLOAD_CONTENT_DECRYPT).flag()
}

sealed class TransportOptions : OptionGroup()

class EwsOptions : TransportOptions() {
    val url by option("--ews-url", valueSourceKey = EWS_URL).required()
    val user by option("--ews-user", valueSourceKey = EWS_USER).required()
    val pass by option("--ews-pass", valueSourceKey = EWS_PASS).prompt(hideInput = true)
}

class SmtpOptions : TransportOptions() {
    val host by option("--smtp-host", valueSourceKey = SMTP_HOST).required()
    val port by option("--smtp-port", valueSourceKey = SMTP_PORT).required()
    val user by option("--smtp-user", valueSourceKey = SMTP_USER).required()
    val pass by option("--smtp-pass", valueSourceKey = SMTP_PASS).prompt(hideInput = true)
}

fun <T : Any> NullableOption<T, T>.requiredPrompt(
    text: String? = null,
    default: String? = null,
    hideInput: Boolean = false,
    requireConfirmation: Boolean = false,
    confirmationPrompt: String = "Repeat for confirmation: ",
    promptSuffix: String = ": ",
    showDefault: Boolean = true
): OptionWithValues<T, T, T> = transformAll(showAsRequired = true) { invocations ->
    val promptText = text ?: "prompt"

    when (val provided = invocations.lastOrNull()) {
        null -> com.github.ajalt.clikt.output.TermUi.prompt(
            promptText, default, hideInput, requireConfirmation,
            confirmationPrompt, promptSuffix, showDefault, context.console
        ) {
            val ctx = OptionCallTransformContext("", this, context)
            transformAll(listOf(transformEach(ctx, listOf(transformValue(ctx, it)))))
        }
        else -> provided
    } ?: throw Abort()
}

fun CliktCommand.context(): Map<String, Any?> {
    return registeredOptions()
        .filterIsInstance<OptionDelegate<*>>()
        .filter { it.valueSourceKey != null && (it.value !is Boolean || it.value is Boolean && it.value == true) }
        .associate { it.valueSourceKey!! to it.value }
}
