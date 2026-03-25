package exporters

import models.Student

/**
 * Interface that every report exporter must implement.
 *
 * POLYMORPHISM via interface: ExcelExporter, HtmlExporter, and JsonExporter
 * all implement this interface. Code that works with [ReportExporter] does
 * not need to know which concrete exporter it has — it just calls [export].
 *
 * This is the DEPENDENCY INVERSION principle: high-level modules (Main)
 * depend on this abstraction, not on concrete classes.
 */
interface ReportExporter {

    /**
     * Produces a report from [students] and [subjectHeaders].
     *
     * @param students       Enriched students with computed grade data.
     * @param subjectHeaders Column names for the score columns.
     * @return               The report as a String (HTML, JSON) or a file path (Excel).
     */
    fun export(students: List<Student>, subjectHeaders: List<String>): String

    /** File extension for this format, e.g. "html", "json", "xlsx". */
    fun fileExtension(): String

    /** Human-readable format name, e.g. "HTML", "JSON", "Excel". */
    fun formatName(): String
}