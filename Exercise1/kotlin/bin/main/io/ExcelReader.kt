package io

import models.Student
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

/**
 * Reads student data from an Excel (`.xlsx`) file using Apache POI.
 *
 * The expected spreadsheet layout is:
 *
 * | StudentID | Name | Subject1 | Subject2 | … |
 * |-----------|------|----------|----------|---|
 *
 * Row 0 is treated as the header row and is skipped when reading data.
 *
 * @property filePath Path to the `.xlsx` file to read.
 */
class ExcelReader(private val filePath: String) {

    /**
     * Reads the first sheet of the Excel file and returns a list of [Student]
     * objects populated with their ID, name, and raw scores.
     *
     * Computed fields (average, grade, GPA, status) are left at their defaults
     * and should be filled in by [services.GradeCalculator].
     */
    fun read(): List<Student> {
        // Step 1 — Open the workbook from the file path using Apache POI's factory
        val workbook = WorkbookFactory.create(File(filePath))

        // Step 2 — Grab the first (and usually only) sheet
        val sheet = workbook.getSheetAt(0)

        val students = mutableListOf<Student>()

        // Step 3 — Iterate over rows, skipping the header at index 0
        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue // Skip null rows

            // Column 0 → Student ID (read as string)
            val studentId = getCellValueAsString(row.getCell(0))

            // Column 1 → Student name
            val name = getCellValueAsString(row.getCell(1))

            // Columns 2+ → Subject scores (read as doubles)
            val scores = mutableListOf<Double>()
            // lastCellNum is 1-based (returns the index of the last cell + 1)
            val lastCol = row.lastCellNum.toInt()
            for (col in 2 until lastCol) {
                val cell = row.getCell(col)
                if (cell != null) {
                    // getNumericCellValue() returns the numeric value of the cell
                    scores.add(cell.numericCellValue)
                }
            }

            // Step 4 — Create a Student with only the raw data
            students.add(
                Student(
                    studentId = studentId,
                    name = name,
                    scores = scores
                )
            )
        }

        // Step 5 — Close the workbook to release file resources
        workbook.close()

        return students
    }

    /**
     * Reads the header row and returns only the subject column names
     * (i.e. everything after StudentID and Name).
     *
     * This is useful for [io.ExcelWriter] so it can reproduce the same subject
     * headers in the output file.
     */
    fun readSubjectHeaders(): List<String> {
        val workbook = WorkbookFactory.create(File(filePath))
        val sheet = workbook.getSheetAt(0)

        // The header row is row 0
        val headerRow = sheet.getRow(0)
        val headers = mutableListOf<String>()

        // Extract column names starting from index 2 (skip StudentID, Name)
        val lastCol = headerRow.lastCellNum.toInt()
        for (col in 2 until lastCol) {
            val cell = headerRow.getCell(col)
            if (cell != null) {
                headers.add(cell.stringCellValue)
            }
        }

        workbook.close()
        return headers
    }

    /**
     * Helper to read a cell value as a [String], regardless of whether the
     * cell is stored as a numeric or string type in Excel.
     */
    private fun getCellValueAsString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.STRING  -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
            else             -> cell.toString()
        }
    }
}
