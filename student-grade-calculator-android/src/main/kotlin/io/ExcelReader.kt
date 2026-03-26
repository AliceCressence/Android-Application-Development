package io

import models.Grade
import models.GraduateStudent
import models.Student
import models.UndergraduateStudent
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

/**
 * Reads student data from an Excel (.xlsx) file.
 *
 * SCOPE FUNCTION — `apply` used when no pattern is needed:
 * workbook is created, used, and closed in a clean chain.
 *
 * No loops — functional range maps throughout.
 */
class ExcelReader(private val filePath: String) {

    fun read(): List<Student> {
        val workbook = WorkbookFactory.create(File(filePath))
        val sheet    = workbook.getSheetAt(0)

        val students = (1..sheet.lastRowNum)
            .mapNotNull { sheet.getRow(it) }
            .map { row ->
                val id    = getCellString(row.getCell(0))
                val name  = getCellString(row.getCell(1))
                val type  = getCellString(row.getCell(2)).lowercase()
                val last  = row.lastCellNum.toInt()
                val scores = (3 until last)
                    .mapNotNull { row.getCell(it) }
                    .map { it.numericCellValue }

                // POLYMORPHISM at creation: right subclass based on type column
                when {
                    type.startsWith("gr") ->
                        GraduateStudent(studentId = id, name = name, scores = scores)
                    else ->
                        UndergraduateStudent(studentId = id, name = name, scores = scores)
                }
            }

        workbook.close()
        return students
    }

    fun readSubjectHeaders(): List<String> {
        val workbook  = WorkbookFactory.create(File(filePath))
        val sheet     = workbook.getSheetAt(0)
        val headerRow = sheet.getRow(0)
        val last      = headerRow.lastCellNum.toInt()
        val headers   = (3 until last)
            .mapNotNull { headerRow.getCell(it) }
            .map { it.stringCellValue }
        workbook.close()
        return headers
    }

    private fun getCellString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.STRING  -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
            else             -> cell.toString()
        }
    }
}