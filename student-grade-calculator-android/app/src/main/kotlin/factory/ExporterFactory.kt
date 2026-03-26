package factory

import exporters.ExcelExporter
import exporters.HtmlExporter
import exporters.JsonExporter
import exporters.ReportExporter

/**
 * Supported export formats.
 *
 * Adding a new format only requires:
 *  1. Adding a value here
 *  2. Adding a branch in [ExporterFactory.create]
 *  3. Creating the new exporter class
 * Nothing else in the codebase needs to change.
 */
enum class ExportFormat {
    EXCEL, HTML, JSON
}

/**
 * FACTORY METHOD PATTERN
 *
 * Centralises the creation of [ReportExporter] objects. Callers request a
 * format and receive the correct exporter — without needing to know which
 * concrete class backs it.
 *
 * Benefits:
 * - Decouples Main from concrete exporter classes
 * - New formats can be added without modifying existing code
 * - Makes testing easy (swap factory in tests)
 *
 * Declared as an `object` (singleton) — there is no reason to have multiple
 * factory instances.
 */
object ExporterFactory {

    /**
     * Creates and returns a [ReportExporter] for the requested [format].
     *
     * FACTORY METHOD: the `when` expression selects the right concrete class
     * based on the requested format — this is the core of the pattern.
     */
    fun create(format: ExportFormat): ReportExporter = when (format) {
        ExportFormat.EXCEL -> ExcelExporter()
        ExportFormat.HTML  -> HtmlExporter()
        ExportFormat.JSON  -> JsonExporter()
    }

    /**
     * Convenience: parse a format string (case-insensitive) into [ExportFormat].
     * Returns null if the string doesn't match any known format.
     *
     * Example: "html" → ExportFormat.HTML
     */
    fun fromString(format: String): ExportFormat? =
        ExportFormat.values().firstOrNull {
            it.name.equals(format.trim(), ignoreCase = true)
        }

    /** Returns all supported format names as a comma-separated string. */
    fun supportedFormats(): String =
        ExportFormat.values().joinToString(", ") { it.name.lowercase() }
}