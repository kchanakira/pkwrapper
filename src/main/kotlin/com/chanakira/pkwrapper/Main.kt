package com.chanakira.pkwrapper

import com.dabomstew.pkrandom.*
import com.dabomstew.pkrandom.romhandlers.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.PrintStream
import kotlin.system.exitProcess

object Main {
    @JvmStatic
    fun main(args: Array<String>) = Randomize().main(args)
}

class Randomize: CliktCommand() {
    private val inputPath by argument("<input>")
    private val outputPath by argument("<output>")
    private val settingsPath by argument("<settings>")
    private val seed by option("-s", "--seed").long().default(RandomSource.pickSeed())
    private val logPath by option("-l", "--log")

    override fun run() {
        val inputFile = File(inputPath)
        val outputFile = File(outputPath)
        val settingsFile = File(settingsPath)
        val settings: Settings

        try {
            Utils.testForRequiredConfigs()
            Utils.validateRomFile(inputFile)

            if (settingsPath.endsWith(".json")) {
                settings = Gson().fromJson(settingsFile.readText(), Settings::class.java)
            }
            else {
                val settingsFileStream = settingsFile.inputStream()
                settings = Settings.read(settingsFileStream)
                settingsFileStream.close()
            }
        }
        catch (e: Utils.InvalidROMException) {
            println("Could not validate the ROM file.")
            exitProcess(1)
        }
        catch (e: IOException) {
            println("Could not parse the settings file.")
            exitProcess(1)
        }

        val handlers = arrayOf(
            Gen1RomHandler.Factory(),
            Gen2RomHandler.Factory(),
            Gen3RomHandler.Factory(),
            Gen4RomHandler.Factory(),
            Gen5RomHandler.Factory())

        for (factory in handlers) {
            if (factory.isLoadable(inputFile.absolutePath)) {
                val handler = factory.create(RandomSource.instance())
                val logBytes = ByteArrayOutputStream()
                val logStream = PrintStream(logBytes, false, "UTF-8")

                handler.setLog(logStream)
                handler.loadRom(inputFile.absolutePath)

                settings.tweakForRom(handler)

                if (settings.romName.isNullOrEmpty()) {
                    settings.romName = handler.romName
                }

                if (settings.customNames == null) {
                    settings.customNames = CustomNamesSet()
                }

                Randomizer(settings, handler).randomize(outputFile.absolutePath, logStream, seed)
                logStream.close()

                if (logPath != null) {
                    val logWriter = File(logPath!!).outputStream()
                    logWriter.write(0xEF)
                    logWriter.write(0xBB)
                    logWriter.write(0xBF)
                    logWriter.write(logBytes.toByteArray())
                    logWriter.close()
                }

                print("Successfully saved " + settings.romName + " ROM to: '" + outputFile.absoluteFile + "'.")
                exitProcess(0)
            }
        }

        println("Could not randomize the ROM: no eligible handlers are available.")
        exitProcess(1)
    }
}
