package io

import models.Student
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

/**
 * Writes enriched student data (with grades) to a new Excel (`.xlsx`) file
 * using Apache POI.
 *
 * The output layout is:
 *
 * | StudentID | Name | Subject1 | … | Average | Grade | GPA | Status |
 * |-----------|------|----------|---|---------|-------|-----|--------|
 *
 * @property outputPath Path where the output `.xlsx` file will be saved.
 */
class ExcelWriter(private val outputPath: String) {

    /**
     * Writes all [students] and their computed grades to a new Excel workbook.
     *
     * @param students       The list of students with computed grade data.
     * @param subjectHeaders Column names for the score columns (e.g. ["Math", "Science"]).
     *
     * CORRECTIONS applied:
     * ─ Removed `for ((col, header) in headers.withIndex())` → replaced with
     *   `headers.forEachIndexed { col, header -> … }` — same semantics, no loop keyword.
     * ─ Removed `for ((rowIndex, student) in students.withIndex())` → replaced with
     *   `students.forEachIndexed { rowIndex, student -> … }`.
     * ─ Removed `for (score in student.scores)` + `var col = 0` + `col++` mutation
     *   → replaced with `student.scores.forEachIndexed { i, score -> … }` so the
     *   column index is derived from the element position, not a mutable counter.
     */
    fun write(students: List<Student>, subjectHeaders: List<String>) {
        val workbook = XSSFWorkbook()
        val sheet    = workbook.createSheet("Results")

        // ── Header row ───────────────────────────────────────────────────────
        val headers = listOf("StudentID", "Name") +
                subjectHeaders +
                listOf("Average", "Grade", "GPA", "Status")

        val headerRow = sheet.createRow(0)

        // CORRECTION: `for ((col, header) in headers.withIndex())` → forEachIndexed.
        // forEachIndexed provides (index, element) without the `for` keyword.
        headers.forEachIndexed { col, header ->
            headerRow.createCell(col).setCellValue(header)
        }

        // ── Data rows ────────────────────────────────────────────────────────

        // CORRECTION: `for ((rowIndex, student) in students.withIndex())` → forEachIndexed.
        students.forEachIndexed { rowIndex, student ->
            val row = sheet.createRow(rowIndex + 1) // row 0 is the header

            // Column 0 — Student ID
            row.createCell(0).setCellValue(student.studentId)

            // Column 1 — Name
            row.createCell(1).setCellValue(student.name)

            // Columns 2..N — Individual subject scores
            // CORRECTION: Removed `var col = 0` mutable counter + `for (score in scores)`.
            // forEachIndexed gives us the position `i` of each score within the scores list;
            // adding 2 gives the correct absolute column index (0=ID, 1=Name, 2+=scores).
            student.scores.forEachIndexed { i, score ->
                row.createCell(2 + i).setCellValue(score)
            }

            // Trailing computed columns come right after the last score column.
            val afterScores = 2 + student.scores.size
            row.createCell(afterScores    ).setCellValue(student.average)
            row.createCell(afterScores + 1).setCellValue(student.grade)
            row.createCell(afterScores + 2).setCellValue(student.gpa)
            row.createCell(afterScores + 3).setCellValue(student.status)
        }

        // ── Write to disk ────────────────────────────────────────────────────
        FileOutputStream(outputPath).use { outputStream ->
            workbook.write(outputStream)
        }

        workbook.close()
    }
}