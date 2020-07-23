package com.chanakira.pkwrapper

import com.dabomstew.pkrandom.CustomNamesSet
import com.dabomstew.pkrandom.RandomSource
import com.dabomstew.pkrandom.Randomizer
import com.dabomstew.pkrandom.romhandlers.*
import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.inputStream
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.clikt.parameters.types.outputStream
import java.io.*
import kotlin.system.exitProcess

class Randomize: CliktCommand(name = "randomize", help = "Randomizes a Pokemon ROM") {
    private val config by argument("<config>", completionCandidates = CompletionCandidates.Path).inputStream()
    private val romInput by argument("<rom input>", completionCandidates = CompletionCandidates.Path).inputStream()
    private val romOutput by argument("<rom output>", completionCandidates = CompletionCandidates.Path).outputStream(truncateExisting = true)
    private val logOutput by option("-l", "--log", completionCandidates = CompletionCandidates.Path).outputStream(truncateExisting = true)
    private val seed by option("-s", "--seed").long().default(RandomSource.pickSeed())

    override fun run() {
        // Universal Pokemon Randomizer uses FileInputStreams everywhere.
        // Use temp files as a transparent workaround.
        val romInputFile = createTempFile()
        val romOutputFile = createTempFile()
        val settings = Helpers.readSettings(config)

        try {
            romInputFile.outputStream().use { romInput.copyTo(it) }
            romInput.close()
        }
        catch (e: IOException) {
            echo("Could not read the input file.", err = true)
            exitProcess(1)
        }

        val handlers = arrayOf(
            Gen1RomHandler.Factory(),
            Gen2RomHandler.Factory(),
            Gen3RomHandler.Factory(),
            Gen4RomHandler.Factory(),
            Gen5RomHandler.Factory()
        )

        // Find a compatible ROM handler.
        for (factory in handlers) {
            if (factory.isLoadable(romInputFile.absolutePath)) {
                val handler = factory.create(RandomSource.instance())
                val logBytes = ByteArrayOutputStream()
                val logStream = PrintStream(logBytes, false, "UTF-8")

                // Load the ROM and setup the log.
                handler.loadRom(romInputFile.absolutePath)
                handler.setLog(logStream)

                // Modify the settings to work for the loaded ROM.
                settings.tweakForRom(handler)

                if (settings.romName == null) {
                    settings.romName = handler.romName
                }

                if (settings.customNames == null) {
                    settings.customNames = CustomNamesSet()
                }

                // Randomize the ROM.
                Randomizer(settings, handler).randomize(romOutputFile.absolutePath, logStream, seed)
                logStream.close()

                // Copy the output from the temp file to the command's output stream.
                romOutputFile.inputStream().use { it.copyTo(romOutput) }
                romOutput.close()

                // Save the log.
                if (logOutput != null) {
                    logOutput!!.write(0xEF)
                    logOutput!!.write(0xBB)
                    logOutput!!.write(0xBF)
                    logOutput!!.write(logBytes.toByteArray())
                    logOutput!!.close()
                }

                // Cleanup the temp files.
                romInputFile.delete()
                romOutputFile.delete()

                return
            }
        }

        echo("No compatible handlers found.", err = true)
        exitProcess(1)
    }
}
