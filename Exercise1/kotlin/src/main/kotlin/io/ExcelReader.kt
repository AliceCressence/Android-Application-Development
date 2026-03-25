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
     *
     * CORRECTIONS applied:
     * ─ Removed `for (rowIndex in 1..sheet.lastRowNum)` → replaced with
     *   `(1..sheet.lastRowNum).mapNotNull { }` — a pure functional range map.
     * ─ Removed inner `for (col in 2 until lastCol)` + `mutableListOf` + `.add()`
     *   → replaced with `(2 until lastCol).mapNotNull { }` functional range map.
     * ─ Removed `mutableListOf<Student>()` accumulator + `.add()` mutation
     *   → the outer map now directly produces the list.
     */
    fun read(): List<Student> {
        val workbook = WorkbookFactory.create(File(filePath))
        val sheet    = workbook.getSheetAt(0)

        // Functional range map — replaces the `for (rowIndex in 1..lastRowNum)` loop.
        // mapNotNull skips any null rows (e.g. empty rows at the end of the sheet).
        val students = (1..sheet.lastRowNum)
            .mapNotNull { rowIndex -> sheet.getRow(rowIndex) }  // skip null rows
            .map { row ->
                val studentId = getCellValueAsString(row.getCell(0))
                val name      = getCellValueAsString(row.getCell(1))
                val lastCol   = row.lastCellNum.toInt()

                // Functional range map — replaces `for (col in 2 until lastCol)` + mutableListOf.
                // mapNotNull safely skips any missing cells in the score columns.
                val scores = (2 until lastCol)
                    .mapNotNull { col -> row.getCell(col) }     // skip null cells
                    .map       { cell -> cell.numericCellValue } // extract numeric value

                Student(studentId = studentId, name = name, scores = scores)
            }

        workbook.close()
        return students
    }

    /**
     * Reads the header row and returns only the subject column names
     * (i.e. everything after StudentID and Name).
     *
     * This is useful for [io.ExcelWriter] so it can reproduce the same subject
     * headers in the output file.
     *
     * CORRECTION: Removed `for (col in 2 until lastCol)` + `mutableListOf` + `.add()`
     * → replaced with `(2 until lastCol).mapNotNull { }` functional range map.
     */
    fun readSubjectHeaders(): List<String> {
        val workbook  = WorkbookFactory.create(File(filePath))
        val sheet     = workbook.getSheetAt(0)
        val headerRow = sheet.getRow(0)
        val lastCol   = headerRow.lastCellNum.toInt()

        // Functional range map — replaces `for (col in 2 until lastCol)` loop.
        val headers = (2 until lastCol)
            .mapNotNull { col -> headerRow.getCell(col) }   // skip null header cells
            .map        { cell -> cell.stringCellValue }     // extract string value

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