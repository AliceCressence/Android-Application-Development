package io

import exporters.ExcelExporter
import java.io.File
import java.util.Base64

/**
 * Interface for file write operations.
 *
 * Separates the "what to write" (exporters) from the "how to write to disk"
 * (this interface). Makes testing easy — swap with an in-memory implementation.
 */
interface FileManager {
    /**
     * Writes [content] to [outputPath].
     * For Excel files the content is Base64-encoded bytes; for others it is
     * plain text. Implementations handle the distinction.
     */
    fun write(content: String, outputPath: String, isBase64: Boolean = false)

    /** Reads the full content of the file at [inputPath] as a String. */
    fun read(inputPath: String): String

    /** Returns true if the file at [path] exists. */
    fun exists(path: String): Boolean
}

/**
 * Default [FileManager] implementation that writes to the real filesystem.
 *
 * Handles both plain-text files (HTML, JSON) and binary files (Excel/Base64).
 */
class DefaultFileManager : FileManager {

    /**
     * Writes [content] to [outputPath].
     *
     * - If [isBase64] is true, decodes the Base64 string to raw bytes first
     *   (used for Excel files produced by [ExcelExporter]).
     * - Otherwise, writes the string directly as UTF-8 text.
     */
    override fun write(content: String, outputPath: String, isBase64: Boolean) {
        val file = File(outputPath)
        file.parentFile?.mkdirs()   // create parent directories if needed

        if (isBase64) {
            file.writeBytes(Base64.getDecoder().decode(content))
        } else {
            file.writeText(content, Charsets.UTF_8)
        }
    }

    override fun read(inputPath: String): String =
        File(inputPath).readText(Charsets.UTF_8)

    override fun exists(path: String): Boolean =
        File(path).exists()
}