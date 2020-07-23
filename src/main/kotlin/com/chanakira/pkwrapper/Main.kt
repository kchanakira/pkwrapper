package com.chanakira.pkwrapper

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object Main {
    @JvmStatic
    fun main(args: Array<String>) = PKWrapper()
        .subcommands(Randomize(), ConvertToJSON(), ConvertToRNQS())
        .main(args)
}

class PKWrapper: CliktCommand() {
    override fun run() = Unit
}
