package com.chanakira.pkwrapper

import com.dabomstew.pkrandom.Settings
import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.inputStream
import com.github.ajalt.clikt.parameters.types.outputStream
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess

class ConvertToRNQS : CliktCommand(name = "rnqs", help = "Converts a JSON file to a randomizer quick settings file") {
    private val input by argument("<input>", completionCandidates = CompletionCandidates.Path).inputStream()
    private val output by argument("<output>", completionCandidates = CompletionCandidates.Path).outputStream(truncateExisting = true)

    override fun run() {
        try {
            val settings = Helpers.readSettings(input)
            val bytes = settings.toString().toByteArray(Charsets.UTF_8)
            
            input.close()

            output.write(Settings.VERSION)
            output.write(bytes.size)
            output.write(bytes)
            output.close()
        }
        catch (e: IOException) {
            echo("An error occurred while converting the settings file.", err = true)
            exitProcess(1)
        }
    }
}
