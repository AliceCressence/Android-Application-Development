package exporters

import models.Student
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.util.Base64

/**
 * Exports student results as an Excel (.xlsx) workbook.
 *
 * POLYMORPHISM: Implements [ReportExporter]. Uses Apache POI internally
 * but exposes exactly the same interface as Html and Json exporters.
 *
 * Because Excel requires binary output (not a plain String), this exporter
 * returns a Base64-encoded string of the workbook bytes. The [FileManager]
 * knows to decode it back when writing to disk.
 *
 * No loops — uses [forEachIndexed] everywhere instead of for/while.
 */
class ExcelExporter : ReportExporter {

    override fun fileExtension(): String = "xlsx"
    override fun formatName(): String    = "Excel"

    /**
     * Builds an XLSX workbook and returns its content as a Base64 string.
     *
     * Layout:
     * | StudentID | Name | Type | Subject… | Average | Grade | GPA | Status |
     */
    override fun export(students: List<Student>, subjectHeaders: List<String>): String {
        val workbook = XSSFWorkbook()
        val sheet    = workbook.createSheet("Results")

        // ── Styles ──────────────────────────────────────────────────────────
        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.DARK_BLUE.index
            fillPattern         = FillPatternType.SOLID_FOREGROUND
            val font = workbook.createFont().also {
                it.color = IndexedColors.WHITE.index
                it.bold  = true
            }
            setFont(font)
        }
        val passStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.LIGHT_GREEN.index
            fillPattern         = FillPatternType.SOLID_FOREGROUND
        }
        val failStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.ROSE.index
            fillPattern         = FillPatternType.SOLID_FOREGROUND
        }

        // ── Header row ───────────────────────────────────────────────────────
        val headers = listOf("StudentID", "Name", "Type") +
                subjectHeaders +
                listOf("Average", "Grade", "GPA", "Status")

        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { col, title ->
            headerRow.createCell(col).also {
                it.setCellValue(title)
                it.cellStyle = headerStyle
            }
        }

        // ── Data rows ────────────────────────────────────────────────────────
        students.forEachIndexed { rowIdx, student ->
            val row      = sheet.createRow(rowIdx + 1)
            val rowStyle = if (student.grade.status() == "PASS") passStyle else failStyle

            row.createCell(0).also { it.setCellValue(student.studentId); it.cellStyle = rowStyle }
            row.createCell(1).also { it.setCellValue(student.name);      it.cellStyle = rowStyle }
            row.createCell(2).also { it.setCellValue(student.studentType()); it.cellStyle = rowStyle }

            student.scores.forEachIndexed { i, score ->
                row.createCell(3 + i).also { it.setCellValue(score); it.cellStyle = rowStyle }
            }

            val after = 3 + student.scores.size
            row.createCell(after    ).also { it.setCellValue(student.average); it.cellStyle = rowStyle }
            row.createCell(after + 1).also { it.setCellValue(student.grade.letter);   it.cellStyle = rowStyle }
            row.createCell(after + 2).also { it.setCellValue(student.grade.gpa);     it.cellStyle = rowStyle }
            row.createCell(after + 3).also { it.setCellValue(student.grade.status());  it.cellStyle = rowStyle }
        }

        // Auto-size all columns for readability
        (0 until headers.size).forEach { sheet.autoSizeColumn(it) }

        // ── Encode as Base64 so we can return a String ───────────────────────
        val bytes = ByteArrayOutputStream()
            .also { workbook.write(it) }
            .toByteArray()
        workbook.close()

        return Base64.getEncoder().encodeToString(bytes)
    }
}