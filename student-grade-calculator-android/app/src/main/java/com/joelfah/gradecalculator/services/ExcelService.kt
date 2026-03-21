package com.joelfah.gradecalculator.services

import android.content.ContentResolver
import android.net.Uri
import com.joelfah.gradecalculator.models.Student
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelService(private val contentResolver: ContentResolver) {

    fun readFromUri(uri: Uri): Pair<List<Student>, List<String>> {
        val inputStream = contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Cannot open URI")
        
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        
        val headerRow = sheet.getRow(0) ?: throw IllegalArgumentException("No header row found")
        val subjectHeaders = mutableListOf<String>()
        // Assume first two columns are ID and Name. Subjects start at index 2
        for (i in 2 until headerRow.lastCellNum) {
            val cell = headerRow.getCell(i)
            subjectHeaders.add(cell?.toString() ?: "Subject ${i - 1}")
        }
        
        val students = mutableListOf<Student>()
        // Data starts at row 1
        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            val idCell = row.getCell(0) ?: continue
            val nameCell = row.getCell(1)

            val studentId = when(idCell.cellType) {
                CellType.NUMERIC -> idCell.numericCellValue.toLong().toString()
                else -> idCell.toString()
            }
            val name = nameCell?.toString() ?: ""
            
            val scores = mutableListOf<Double>()
            for (j in 2 until headerRow.lastCellNum) {
                val cell = row.getCell(j)
                val score = when (cell?.cellType) {
                    CellType.NUMERIC -> cell.numericCellValue
                    CellType.STRING -> cell.stringCellValue.toDoubleOrNull() ?: 0.0
                    else -> 0.0
                }
                scores.add(score)
            }
            
            students.add(Student(studentId = studentId, name = name, scores = scores))
        }
        
        workbook.close()
        inputStream.close()
        return Pair(students, subjectHeaders)
    }

    fun writeToUri(uri: Uri, students: List<Student>, headers: List<String>) {
        val pfd = contentResolver.openFileDescriptor(uri, "w") ?: throw IllegalArgumentException("Cannot open file for writing")
        val outputStream = java.io.FileOutputStream(pfd.fileDescriptor)
        
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Grades")
        
        // Header
        val headerRow = sheet.createRow(0)
        val headerColumns = listOf("StudentID", "Name") + headers + listOf("Average", "Grade", "GPA", "Status")
        
        headerColumns.forEachIndexed { index, title ->
            headerRow.createCell(index).setCellValue(title)
        }
        
        // Data
        students.forEachIndexed { index, student ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(student.studentId)
            row.createCell(1).setCellValue(student.name)
            
            student.scores.forEachIndexed { i, score ->
                row.createCell(2 + i).setCellValue(score)
            }
            
            val offset = 2 + student.scores.size
            row.createCell(offset).setCellValue(student.average)
            row.createCell(offset + 1).setCellValue(student.grade)
            row.createCell(offset + 2).setCellValue(student.gpa)
            row.createCell(offset + 3).setCellValue(student.status)
        }
        
        workbook.write(outputStream)
        workbook.close()
        outputStream.close()
        pfd.close()
    }
}
