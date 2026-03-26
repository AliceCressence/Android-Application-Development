package io

import android.content.Context
import android.net.Uri
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
class ExcelReader(private val filePath: String = "") {

    // Original constructor for file path
    constructor() : this("")

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

    /**
     * New method to read Excel file from Uri (for Android content provider)
     * Returns Triple of (rawStudents, subjectNames, scoreMap)
     */
    fun readExcelFile(
        uri: Uri,
        context: Context
    ): Triple<List<Triple<String, String, Boolean>>, List<String>, Map<String, List<Double>>> {

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open file")

        inputStream.use { stream ->
            val workbook = WorkbookFactory.create(stream)
            val sheet = workbook.getSheetAt(0)

            val rawStudents = mutableListOf<Triple<String, String, Boolean>>()
            val scoreMap = mutableMapOf<String, MutableList<Double>>()
            val subjectNames = mutableListOf<String>()

            // Parse headers (first row)
            val headerRow = sheet.getRow(0)
            if (headerRow != null) {
                val last = headerRow.lastCellNum.toInt()
                // Skip first 3 columns (ID, Name, Type)
                for (i in 3 until last) {
                    val cell = headerRow.getCell(i)
                    subjectNames.add(getCellString(cell))
                }
            }

            // Parse student data rows
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue

                val studentId = getCellString(row.getCell(0))
                val studentName = getCellString(row.getCell(1))
                val studentType = getCellString(row.getCell(2)).lowercase()
                val isGraduate = studentType.startsWith("gr")

                rawStudents.add(Triple(studentId, studentName, isGraduate))

                // Parse scores (columns 3 onwards)
                val scores = mutableListOf<Double>()
                val last = row.lastCellNum.toInt()
                for (j in 3 until last) {
                    val cell = row.getCell(j)
                    scores.add(getNumericValue(cell))
                }
                scoreMap[studentId] = scores
            }

            workbook.close()
            return Triple(rawStudents, subjectNames, scoreMap)
        }
    }

    private fun getCellString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.STRING  -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
            else             -> cell.toString()
        }
    }

    private fun getNumericValue(cell: org.apache.poi.ss.usermodel.Cell?): Double {
        if (cell == null) return 0.0
        return when (cell.cellType) {
            CellType.NUMERIC -> cell.numericCellValue
            CellType.STRING -> cell.stringCellValue.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }
}