package com.chanakira.pkwrapper

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.inputStream
import com.github.ajalt.clikt.parameters.types.outputStream
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.IOException
import kotlin.system.exitProcess

class ConvertToJSON: CliktCommand(name = "json", help = "Converts a randomizer quick settings file to JSON") {
    private val input by argument("<input>", completionCandidates = CompletionCandidates.Path).inputStream()
    private val output by argument("<output>", completionCandidates = CompletionCandidates.Path).outputStream(truncateExisting = true)
    private val pretty by option("-p", "--pretty").flag()

    override fun run() {
        try {
            val gson = if (pretty) GsonBuilder().setPrettyPrinting().create() else Gson()
            val settings = Helpers.readSettings(input)
            val bytes = gson.toJson(settings).toByteArray(Charsets.UTF_8)

            input.close()

            output.write(bytes)
            output.close()
        }
        catch (e: IOException) {
            echo("An error occurred while converting the settings file.", err = true)
            exitProcess(1)
        }
    }
}
