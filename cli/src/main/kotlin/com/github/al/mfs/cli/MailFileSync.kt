package com.github.al.mfs.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.al.mfs.cli.command.Init
import com.github.al.mfs.cli.command.ReceiveCommand
import com.github.al.mfs.cli.command.SendCommand
import java.nio.file.Paths

class MailFileSync : CliktCommand() {

    init {
        context {
            valueSources(
                PropertiesValueSource(
                    Paths.get(
                        System.getProperty("user.home"),
                        ".mailfilesync.properties"
                    ).toString()
                )
            )
        }
    }

    override fun run() {
    }
}

fun main(args: Array<String>) = MailFileSync()
    .subcommands(Init(), SendCommand(), ReceiveCommand())
    .main(args)
