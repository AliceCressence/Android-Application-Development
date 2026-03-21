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
     */
    fun write(students: List<Student>, subjectHeaders: List<String>) {
        // Step 1 — Create a brand-new XSSF (xlsx) workbook
        val workbook = XSSFWorkbook()

        // Step 2 — Create a sheet named "Results"
        val sheet = workbook.createSheet("Results")

        // Step 3 — Build and write the header row
        val headers = listOf("StudentID", "Name") +
                subjectHeaders +
                listOf("Average", "Grade", "GPA", "Status")

        // createRow(0) creates the first row (header)
        val headerRow = sheet.createRow(0)
        for ((col, header) in headers.withIndex()) {
            // createCell(col) creates a cell at the given column index
            headerRow.createCell(col).setCellValue(header)
        }

        // Step 4 — Write one row per student
        for ((rowIndex, student) in students.withIndex()) {
            // Data rows start at index 1 (row 0 is the header)
            val row = sheet.createRow(rowIndex + 1)
            var col = 0

            // Column 0 — Student ID
            row.createCell(col++).setCellValue(student.studentId)

            // Column 1 — Name
            row.createCell(col++).setCellValue(student.name)

            // Columns 2..N — Individual subject scores
            for (score in student.scores) {
                row.createCell(col++).setCellValue(score)
            }

            // Average column
            row.createCell(col++).setCellValue(student.average)

            // Grade column
            row.createCell(col++).setCellValue(student.grade)

            // GPA column
            row.createCell(col++).setCellValue(student.gpa)

            // Status column
            row.createCell(col++).setCellValue(student.status)
        }

        // Step 5 — Write the workbook to disk using a FileOutputStream
        FileOutputStream(outputPath).use { outputStream ->
            workbook.write(outputStream)
        }

        // Step 6 — Close the workbook to free resources
        workbook.close()
    }
}
