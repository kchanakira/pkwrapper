package com.chanakira.pkwrapper

import com.dabomstew.pkrandom.FileFunctions
import com.dabomstew.pkrandom.Settings
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.InputStreamReader

object Helpers {
    fun readSettings(input: InputStream): Settings {
        // Attempt to read the settings from the stream as JSON.
        // When that fails, attempt to read the settings from the stream as RNQS.

        // Use a buffered stream for reset support.
        val bufferedStream = BufferedInputStream(input)

        try {
            // Mark the beginning of the stream.
            bufferedStream.mark(0)

            return Gson().fromJson<Settings>(InputStreamReader(bufferedStream), Settings::class.java)
        }
        catch (e: JsonSyntaxException) {
            // Rewind to the beginning of the stream.
            bufferedStream.reset()

            val version = bufferedStream.read()

            if (version > Settings.VERSION) {
                throw UnsupportedOperationException("Cannot read settings from a newer version of the randomizer.")
            }

            val length: Int = bufferedStream.read()
            val buffer = FileFunctions.readFullyIntoBuffer(bufferedStream, length)
            val content: String? = String(buffer, Charsets.UTF_8)

            return Settings.fromString(content)
        }
        catch (e: Exception) {
            // Give up on life.
            throw UnsupportedOperationException("Unsupported settings file format.")
        }
        finally {
            bufferedStream.close()
            input.close()
        }
    }
}
